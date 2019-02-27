<template>
    <div>
        <Row>
            <Button class="tableBtn" @click="handleAddClick">新增</Button>

        </Row>
        <Table border :columns="columns" :data="tableData" highlight-row @on-current-change="handleCurrentChange"></Table>
        <Page :current="curPage" :total="totalCount" :page-size="pageSize" @on-change="handlePageChange"
              show-total />

        <!--<Modal v-model="formVisible" :title="formTitle" :model="parser" @on-visible-change="handleVisibleChange" @on-ok="handleFormOk" footer-hide>-->
        <Modal v-model="formVisible" :title="formTitle" @on-visible-change="handleVisibleChange" @on-ok="handleFormOk" footer-hide>
            <Form :label-width="80" ref="parserForm" :model="masterSlave">
                <FormItem prop="name" label="目标名称">
                    <Input v-model="masterSlave.name"/>
                </FormItem>
                <FormItem prop="type" label="目标类型">
                    <Input v-model="masterSlave.type"/>
                </FormItem>

                <FormItem prop="masterTable" label="主表">
                     <Select v-model="masterSlave.masterTableId" filterable>
                        <Option v-for="item in tableNames" :value="item.id" :key="item.id">{{ item.tableChinese }}</Option>
                    </Select>
                </FormItem>
                <FormItem prop="slaveTable" label="从表">
                    <Select v-model="masterSlave.slaveTableId" filterable>
                        <Option v-for="item in tableNames" :value="item.id" :key="item.id">{{ item.tableChinese }}</Option>
                    </Select>
                </FormItem>
                <FormItem prop="image" label="图片">
                    <el-upload  v-model="masterSlave.image"
                        class="upload-idCard"
                        ref="elUpload"
                        :class=" canAddPic  ? 'uploadSuccess': ''"
                        action="mvc/control/imgUpload"
                        :on-success="handleUploadSuccess"
                        :on-remove="handleRemove"
                        list-type="picture-card"
                        accept="image/*"
                        :limit="imgLimit"
                        :file-list="productImgs"
                        :on-preview="handlePictureCardPreview"
                        :before-upload="beforeAvatarUpload"
                    >
                        <i class="el-icon-plus"></i>
                    </el-upload>
                    <el-dialog :visible.sync="dialogVisible">
                        <img width="100%" :src="dialogImageUrl" alt="">
                    </el-dialog>
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
    </div>
</template>
<script>
    import BlockManage from './BlockManage.vue'
    export default {
        components: {
            BlockManage
        },
        computed: {
            canAddPic() {
                return this.uploadSuccessState || (this.productImgs.length > 0)
            }
        },
        data () {
            return {
                uploadSuccessState:false,
                tableNames:[],
                tableNames2:[],
                 masterSlave: {
                    name: '',
                    type: '',
                    masterTable:'',
                    slaveTable:'',
                     image: ''
                },

                //图片
                dialogImageUrl: '',
                dialogVisible: false,
                imgLimit: 1,
                productImgs: [],

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
                    type: [
                        { required: true, message: '类别为必填项', trigger: 'blur' }
                    ],
                    masterTable: [
                        { required: true, message: '主表为必填项', trigger: 'blur' }
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
                        key: 'masterTableName'
                    },
                    {
                        title: '从表',
                        key: 'slaveTableName'
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
                uploadList:[],
                loadingStatus: false
            }
        },
        created() {
            this.getData();
            this.getTables();
            // 报错了
            // this.getTables2();
        },
        methods: {
            //修改操作
            update (row) {
                row.file = null;
                this.formTitle = '修改';
                if(row.id){
                    let me = this;
                    this.$axios.post('mvc/masterSlave/findOne', {
                        id:row.id
                    }).then(res => {
                        me.masterSlave = res.data;
                        me.productImgs = [{url: row.image}];
                        this.formVisible = true;
                    }).catch(e => {

                        // this.formVisible = true;
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
                this.$axios.post('mvc/masterSlave/delete', {
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
                this.$axios.post('mvc/masterSlave/page', {
                    page: this.curPage,
                    limit: this.pageSize
                }).then(res => {
                    this.tableData = res.data.list;
                    this.totalCount = res.data.count;
                });
            },
            //添加按钮点击
            handleAddClick() {
                this.masterSlave ={
                    name: '',
                    type: '',
                    masterTable:'',
                    slaveTable:'',
                    image: ''
                };
                this.formVisible = true;
                this.getTables();
                // 报错了
                // this.getTables2();

            },
            //编辑弹框显示状态监听
            handleVisibleChange(val) {
                if (!val) {
                    //清空
                    this.$refs.elUpload.clearFiles();
                    this.productImgs=[];
                    this.uploadSuccessState = false;
                    this.$refs['parserForm'].resetFields();
                }
            },
            //处理编辑弹框确认操作
            handleFormOk() {
                const {name, type,masterTableId,slaveTableId} = this.masterSlave;
                if ( !name || !type || !masterTableId || !slaveTableId) {
                this.$message.warning('请填写完整表单, 并上传图片')
                    return
                }
                let isValid = true;
                this.$refs['parserForm'].validate((valid) => {
                    if (!valid) {
                        isValid = false;
                    }
                })
                if (!isValid) {
                    return ;
                }
                if (this.masterSlave.id) {
                    this.postOperate('mvc/masterSlave/update');
                } else {
                    this.postOperate('mvc/masterSlave/add');
                }
            },
            //请求后台
            postOperate(url) {
                var me = this;
                this.$axios.post(url,this.masterSlave)
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
             getTables() {
                this.$axios.post('mvc/getAllTables').then(res => {

                    this.tableNames = res.data;
                })
            },

            handleUploadSuccess(res, file) {//图片上传成功
                const {success, error, url} = res
                if (!success) {
                    this.$message.error(error);
                    this.$refs.elUpload.clearFiles()
                    return
                }
                // this.masterSlave.image = url;
                this.uploadSuccessState = true;
            },
            handleRemove(file, fileList) {//移除图片
                this.uploadSuccessState = false;
                this.productImgs = [];
            },

            handlePictureCardPreview(file) {//预览图片时调用
                this.dialogImageUrl = file.url;
                this.dialogVisible = true;
            },
            beforeAvatarUpload(file) {//文件上传之前调用做一些拦截限制

                // const isJPG = file.type === 'image/jpeg';
                const isLt2M = file.size / 1024 / 1024 < 2;

                if (!isLt2M) {
                    this.$message.error('上传图片大小不能超过 2MB!');
                }
                return isLt2M;
            },
        }
    }
</script>
<style>
    .tableBtn {
        margin: 5px;
    }

     .uploadSuccess .el-upload.el-upload--picture-card{
         display:none;
     }
    .uploadImgMask .el-dialog__wrapper{
        z-index:999999 !important;
    }
    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }

    .avatar-uploader .el-upload:hover {
        border-color: #409EFF;
    }

    .el-upload__input {
        display: none !important;
    }

    .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        line-height: 178px;
        text-align: center;
    }

    /* .avatar {
         cancelCrop width: 178px;
         height: 178px;
         display: block;
     }
 */
    .proper-wrap {
        margin-bottom: 12px;
    }

</style>
