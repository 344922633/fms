<template>
    <div>
        <Row>
            <Button class="tableBtn" @click="handleAddClick">新增</Button>

        </Row>
        <Table border :columns="columns" :data="tableData" highlight-row @on-current-change="handleCurrentChange"></Table>
        <Page :current="curPage" :total="totalCount" :page-size="pageSize" @on-change="handlePageChange"
              show-total />

        <Modal v-model="formVisible" :title="formTitle" :model="parser" @on-visible-change="handleVisibleChange" @on-ok="handleFormOk" footer-hide>
            <Form :label-width="80" ref="parserForm" :model="parser" :rules="ruleValidate">
                <FormItem prop="name" label="关系名称">
                    <Input v-model="parser.name"/>
                </FormItem>
                <FormItem prop="source" label="主表">
                    <!--<Input v-model="parser.name"/>-->
                    <Select v-model="parser.mainTable" @on-change="getParserClassNameList" clearable>
                        <Option v-for="(item,index) in parserNameList" :value="item.path" :key="index" >{{ item.name }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="从表">
                    <Select v-model="parser.fromTbale" @on-change="getParserClassNameList" clearable>
                        <Option v-for="(item,index) in parserNameList" :value="item.path" :key="index" >{{ item.name }}</Option>
                    </Select>
                </FormItem>

                <FormItem>
                    <Button type="success" long @click="handleFormOk">提交</Button>
                </FormItem>
            </Form>
        </Modal>
        <Modal
            title="提示"
            v-model="modalDelete"
            :mask-closable="false"
            @on-ok="remove">
            <p>确认要删除数据吗？</p>
        </Modal>
        <Modal
            v-model="uploadJarMod"
            title="解析器jar包上传"
            width="300">
            <Upload
                ref="upload"
                multiple
                :before-upload="handleUpload"
                action="mvc/uploadJars"
                :on-success="uploadSuccess"
                :on-remove="handleRemove"
                :on-error="uploadError">
                <Button icon="ios-cloud-upload-outline">上传</Button>

            </Upload>
            <Button style="margin-top: 5px" type="text" @click="jarFileUpload" :loading="loadingStatus">{{ loadingStatus ? 'Uploading' : '上传' }}</Button>
        </Modal>
    </div>
</template>
<script>
    import BlockManage from '../BlockManage.vue'
    export default {
        components: {
            BlockManage
        },
        data () {
            return {
                parserExtList:[

                ],
                param1:"参数1",
                paramDesc1:"参数1的描述",
                parserNameList:[],
                parserClassNameList:[],
                parserMethodNameList:[],
                parserClassAndMethods:[],
                uploadJarMod:false,//解析器jar包上传窗口
                blockVisible: false,
                //删除弹框显示标示
                modalDelete:false,
                //当前选中id
                seleteId:'',
                //表格数据
                tableData: [],
                //当前页数
                curPage: 1,
                //总条数
                totalCount: 0,
                //每页条数
                pageSize: 5,
                //文件列表
                fileList: [],
                //当前选中文件
                current: null,
                //当前解析器
                parser: {
                    id:'',
                    name: '',
                    mainTable: '',
                    fromTbale:''
                },
                blockInfo: {},
                //编辑框显示标示
                formVisible: false,
                //编辑框form校验
                ruleValidate: {
                    name: [
                        { required: true, message: '关系名称为必填项', trigger: 'blur' }
                    ],
                    mainTable: [
                        { required: true, message: '主表为必填项', trigger: 'blur' }
                    ],
                    fromTbale: [
                        { required: true, message: '从表为必填项', trigger: 'blur' }
                    ]
                },
                //编辑框标题
                formTitle: '新增',
                columns: [
                    {
                        title: '关系名称',
                        key: 'name'
                    },
                    {
                        title: '主表',
                        key: 'mainTable'
                    },
                    {
                        title: '从表',
                        key: 'fromTbale'
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 250,
                        align: 'center',
                        render: (h, params) => {
                            return h('div', [
                                h('Button', {
                                    props: {
                                        type: 'primary',
                                        size: 'small'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            this.update(params.row)
                                        }
                                    }
                                }, '修改'),
                                h('Button', {
                                    props: {
                                        type: 'error',
                                        size: 'small'
                                    },
                                    on: {
                                        click: () => {
                                            this.deleteInfo(params.row)
                                        }
                                    }
                                }, '删除')
                            ]);
                        }
                    }
                ],
                data: [
                    {
                        name: 'John Brown',
                        age: 18,
                        address: 'New York No. 1 Lake Park'
                    },
                    {
                        name: 'Jim Green',
                        age: 24,
                        address: 'London No. 1 Lake Park'
                    },
                    {
                        name: 'Joe Black',
                        age: 30,
                        address: 'Sydney No. 1 Lake Park'
                    },
                    {
                        name: 'Jon Snow',
                        age: 26,
                        address: 'Ottawa No. 2 Lake Park'
                    }
                ],
                options: {//文件分片上传数据配置
                    target: process.env.BASE_UPLOAD + 'mvc/chunk',
                    testChunks: true,
                    simultaneousUploads: 1,
                    preprocess: this.preprocess,
                    chunkSize: 1024 * 1024 * 5
                },
                statusText: {
                    success: '成功了',
                    error: '出错了',
                    uploading: '上传中',
                    paused: '暂停中',
                    waiting: '等待中'
                },
                uploadList:[],
                loadingStatus: false
            }
        },
        created() {
            this.getData();
        },
        methods: {
            handleUpload(file){
                this.uploadList.push(file);
                file.status = 'finished'
                this.$refs.upload.fileList.push(file)

                // this.$refs.upload.fileList.push(file);
                return false;
            },
            jarFileUpload () {
                this.loadingStatus = true;
                let jarFileNameList=[];
                let flag = true;

                this.uploadList.forEach(file => {
                    if(!file.name.endsWith('.jar')){
                        flag = false;
                    }
                    jarFileNameList.push(file.name)
                })
                if (!flag) {
                    this.$notify({
                        title: '提示',
                        message: '只允许上传jar文件！',
                        type: 'error'
                    })
                    this.loadingStatus = false;
                    return ;
                }

                this.$axios.post('mvc/fileParserJar/checkJarList', {
                    fileNameList:jarFileNameList.join(',')
                }).then(res => {
                    if(res.data.success){
                        this.$refs.upload.clearFiles();
                        if (this.uploadList) {
                            this.uploadList.forEach(file => {
                                this.$refs.upload.post(file);
                            })
                        }
                    }else{
                        this.$Modal.confirm({
                            title:'确认窗口',
                            content:res.data.data,
                            onOk: () => {
                                this.$refs.upload.clearFiles();
                                if (this.uploadList) {
                                    this.uploadList.forEach(file => {
                                        this.$refs.upload.post(file);
                                    })
                                }
                                this.loadingStatus = false;
                            },
                            onCancel:()=>{
                                this.loadingStatus = false;
                            }
                        })
                    }
                });
            },
            handleRemove(file, fileList) {
                let idx = this.uploadList.findIndex((f) => {
                    return f.name == file.name;
                })
                this.uploadList.splice(idx, 1);
            },
            uploadSuccess(response, file, fileList){
                console.log(file)
                console.log(fileList)
                this.loadingStatus = false;
                this.$Message.success('This is a success tip');
            },
            uploadError(){
                this.loadingStatus = false;
                this.$Message.error('This is an error tip');
            },
            handleAddJarClick(){
                this.$refs.upload.clearFiles();
                this.uploadList = [];
                this.uploadJarMod=true
            },
            //修改操作
            update (row) {
                this.getParserNameList();
                row.file = null;
                this.formTitle = '修改';
                if(row.source){
                    this.$axios.post('mvc/fileParserJar/getJarClassAndMethodList', {
                        path:row.source
                    }).then(res => {
                        this.parserClassAndMethods = res.data;
                        this.parserClassNameList = this.parserClassAndMethods.classNames;
                        this.parserMethodNameList = this.parserClassAndMethods[row.className]
                        // this.parser.methodName='';
                        this.parser = JSON.parse(JSON.stringify(row));
                        this.parser.inputType = this.parser.inputType + '';
                        this.formVisible = true;
                    }).catch(e => {

                        this.parser = JSON.parse(JSON.stringify(row));
                        this.parser.inputType = this.parser.inputType + '';
                        this.formVisible = true;
                    });
                    this.$axios.post('mvc/fileParser/getParamList', {
                        parserId:row.id
                    }).then(res => {
                        this.parserExtList = res.data;
                        this.formVisible = true;
                    }).catch(e => {
                        this.formVisible = true;
                    });

                }
            },
            //删除操作
            deleteInfo(row){
                this.seleteId=row.id
                this.modalDelete=true;
            },
            //删除确认
            remove (row) {
                var me = this;
                this.$axios.post('mvc/fileParser/deleteParser', {
                    id: this.seleteId
                }).then(res => {
                    me.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                    if (res.data.success) {
                        me.formVisible = false;
                        me.getData()
                    }
                });
            },
            //页码变更
            handlePageChange(val) {
                this.curPage = val;
                this.getData();
            },
            //加载数据
            getData() {
                this.$axios.post('mvc/fileParser/page', {
                    page: this.curPage,
                    limit: this.pageSize
                }).then(res => {
                    this.tableData = res.data.list;
                    this.totalCount = res.data.count;
                });
            },
            getParserNameList(){
                this.$axios.post('mvc/fileParserJar/getJarList', {
                }).then(res => {
                    this.parserNameList = res.data;
                });
            },
            getParserClassNameList(){
                if (this.$refs.parserClassNameRef) {
                    this.$refs.parserClassNameRef.clearSingleSelect();
                    this.$refs.parserMethodNameRef.clearSingleSelect();
                }
                // this.parser.methodName='';
                // this.parser.className='';
                if(this.parser.source){
                    this.$axios.post('mvc/fileParserJar/getJarClassAndMethodList', {
                        path:this.parser.source
                    }).then(res => {
                        this.parserClassAndMethods = res.data;
                        this.parserClassNameList = this.parserClassAndMethods.classNames;
                        // this.parser.methodName='';
                    });
                }

            },
            getParserMethodNameList(){
                // this.parser.methodName='';
                // if(this.parser.className!=''){
                    this.parserMethodNameList = this.parserClassAndMethods[this.parser.className]
                // }

                if(this.parser.className){
                    this.$axios.post('mvc/fileParserJar/getJarClassParamList', {
                          path:this.parser.source,
                          className:this.parser.className
                     }).then(res => {
                         this.parserExtList = res.data;
                      });
                 }

            },
            //添加按钮点击
            handleAddClick() {
                this.parser = {
                    name: '',
                    inputType: '0',
                    file: null
                };
                this.parserClassAndMethods = [];
                this.parserClassNameList = [];
                this.getParserNameList();
                this.formVisible = true;

            },
            //编辑弹框显示状态监听
            handleVisibleChange(val) {
                if (!val) {
                    this.$refs['parserForm'].resetFields();
                }
            },
            //上传文件格式校验
            handleFormatError(file) {
                this.$notify({
                    title: '提示',
                    message: '文件格式不正确！',
                    type: 'error'
                });
            },
            //处理文件上传前操作
            handleBeforeUpload(file) {
                this.parser.file = file;
                return false;
            },
            //处理编辑弹框确认操作
            handleFormOk() {

                console.info(this.paramList)
                let isValid = true;
                this.$refs['parserForm'].validate((valid) => {
                    if (!valid) {
                        isValid = false;
                    }
                })
                if (!isValid) {
                    return ;
                }
                if (this.parser.file) {
                    this.$refs.upload.post(this.parser.file);
                } else {
                    if (this.parser.id) {
                        this.postOperate('mvc/fileParser/updateParser');
                    } else {
                        this.postOperate('mvc/fileParser/addParser');
                    }
                }
            },
            //文件上传成功回掉
            handleUploadSuccess(response, file, fileList) {
                if (response.success) {
                    this.parser.source = response.data;
                    if (this.parser.id) {
                        this.postOperate('mvc/fileParser/updateParser');
                    } else {
                        this.postOperate('mvc/fileParser/addParser');
                    }
                } else {
                    this.$notify({
                        title: '提示',
                        message: response.data,
                        type: 'error'
                    });
                }
            },
            //请求后台
            postOperate(url) {
                var me = this;
                this.parser.parserExt = JSON.stringify(this.parserExtList);
                this.$axios.post(url,this.parser)
                    .then(res => {
                        me.$notify({
                            title: '提示',
                            message: res.data.data,
                            type: 'success'
                        });
                        if (res.data.success) {
                            me.formVisible = false;
                            me.getData()
                        }
                    });
            },
            //当前选中行变更
            handleCurrentChange(c) {
                this.current = c;
            },
            //打开黑白名单弹框
            handleBlock() {
                if (!(this.current && this.current.id)) {
                    this.$notify({
                        title: '提示',
                        message: '请选择一条数据',
                        type: 'error'
                    })
                    return ;
                }
                let me = this;
                me.blockInfo = {};
                this.$axios.post('mvc/blockManage/getList', {
                    fileParserId: this.current.id
                }).then(res => {
                    if (res.data.length > 0) {
                        me.blockInfo = res.data[0];
                    }

                    this.blockVisible = true;
                })
            }
        }
    }
</script>
<style scoped>
    .tableBtn {
        margin: 5px;
    }
</style>
