<template>
    <div class="table">
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon-lx-cascades"></i>文件分类管理</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-button type="primary" icon="button" @click="handleAdd">添加</el-button>
            </div>
            <div class="pagination">
                <Table class="tableClass" :columns="columns" :data="tableData"  @on-selection-change="selectRowChange" >

                </Table>
                <div class="tablePage">
                    <Page :total="total" :page-size="pageSize" :page-size-opts="pageSizeOpts" :current="current" @on-change="handleCurrentChange" @on-page-size-change="handleSizeChange" show-sizer />
                </div>
            </div>
        </div>


        <!-- 编辑弹出框 -->
        <el-dialog :title="title" :visible.sync="editVisible" width="30%">
            <el-form ref="form" :model="form" label-width="50px">
                <el-form-item label="分类名称"  label-width="100px">
                    <el-input v-model="form.name"></el-input>
                </el-form-item>
                <el-form-item label="父分类" label-width="100px">
                    <el-select v-model="form.type">
                      <el-option label="数据库类文件" value="1"></el-option>
                      <el-option label="拓扑结构类文件" value="2"></el-option>
                      <el-option label="结构化文件" value="3"></el-option>
                      <el-option label="文本类文件" value="4"></el-option>
                      <el-option label="配置类文件" value="5"></el-option>
                      <el-option label="日志类文件" value="6"></el-option>
                      <el-option label="程序类文件" value="7"></el-option>
                    </el-select>
                  </el-form-item>

                <el-form-item label="文件后缀" label-width="100px">
                    <el-input v-model="form.fileSuffix"></el-input>
                </el-form-item>
               <el-form-item label="文件解析器" label-width="100px">

                   <!-- <el-input v-model="form.fileParserIds"></el-input>-->
                   <el-checkbox v-for="item in fileParserList" :label="item.id" :key="item.id" v-model="checked">{{item.name}}</el-checkbox>
                  <!--  <CheckboxGroup v-model="checkAllGroup" v-for="item in fileParserList">
                        <Checkbox label="{item.key}" :key="item.id" :value="item.id"></Checkbox>
                    </CheckboxGroup>-->
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">确 定</el-button>
            </span>
        </el-dialog>

        <!-- 删除提示框 -->
        <el-dialog title="提示" :visible.sync="delVisible" width="300px" center>
            <div class="del-dialog-cnt">删除不可恢复，是否确定删除？</div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="delVisible = false">取 消</el-button>
                <el-button type="primary" @click="deleteRow">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: 'basetable',
        data() {
            return {
                //获取分类url
                url: 'mvc/fileType/getList',
                //操作分类 url
                addOrupdateUrl:'',
                //弹框标题
                title:'',
                columns: [

                    {
                        title: '分类名称',
                        key: 'name'
                    },
                    {
                        title: '文件后缀',
                        key: 'fileSuffix'
                    },
                    {
                        title: '文件解析器',
                        key: 'fileParserNames'
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 150,
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
                                            this.handleEdit(params.index, params.row)
                                        }
                                    }
                                }, '编辑'),
                                h('Button', {
                                    props: {
                                        type: 'error',
                                        size: 'small'
                                    },
                                    on: {
                                        click: () => {
                                            this.handleDelete(params.index, params.row)
                                        }
                                    }
                                }, '删除')
                            ]);
                        }
                    }
                ],
                //列表数据
                tableData: [],
                //当前选中行id
                id:-1 ,
                //编辑框显示标示
                editVisible: false,
                //删除框显示标示
                delVisible: false,
                //分页总条数
                total:0,
                //单页条数
                pageSize:10,
                //当前页
                current:1,
                //分页组
                pageSizeOpts:[10,20,40,100],
                form: {
                    name: '',
                    fileSuffix: '',
                    fileParserIds: ''
                },
                //文件解析器
                fileParserList:[],
                //选中行
                checked: [],
                //当前操作行下标
                idx: -1
            }
        },
        created() {
            this.getData();
        },
        methods: {
            // 分页导航
            handleCurrentChange(val) {
                this.current = val;
                this.getData();
            },
            //单页条数变更
            handleSizeChange(val) {
                this.pageSize = val;
                this.getData();
            },
            // 获取分类数据
            getData() {
                this.$axios.post(this.url, {
                    page: this.current,
                    limit: this.pageSize
                }).then((res) => {
                    this.tableData = res.data.list;
                    this.total = res.data.count;
                })
            },
            //列格式化
            formatter(row, column) {
                return row.address;
            },
            //过滤标签
            filterTag(value, row) {
                return row.tag === value;
            },
            //处理修改操作
            handleEdit(index, row) {
                this.addOrupdateUrl = "mvc/fileType/update";
                this.$axios.get("mvc/fileParser/getList",{
                }).then((res) => {
                    this.fileParserList = res.data;
                });
                this.checked=[];
                this.title = "编辑";
                this.$axios.post("mvc/fileType/getById", {
                    id: row.id
                }).then((res) => {
                    if(res.data != null) {
                        this.idx = index;
                        this.id=row.id;
                        const item = this.tableData[index];
                        this.form = {
                            name: res.data.name,
                            type: res.data.type,
                            fileSuffix: res.data.fileSuffix
                        };
                        var fileParserIdsArr = [];
                        if (res.data.fileParserIds.length > 0) {
                            fileParserIdsArr =  res.data.fileParserIds.split(",");
                        }
                        for(let parserId of fileParserIdsArr) {
                            this.checked.push(Number(parserId));
                        }

                        this.editVisible = true;
                    } else {
                        this.getData();
                    }
                })
            },
            //处理删除操作
            handleDelete(index, row) {
                this.id=row.id;
                this.idx = index;
                this.delVisible = true;
            },
            //处理添加操作
            handleAdd() {
                this.form = {
                    name: '',
                    fileSuffix: '',
                    fileParserIds:''
                };
                this.id = null;
                this.title = "添加";
                this.checked = [];
                this.editVisible = true;
                this.addOrupdateUrl = "mvc/fileType/add";
                this.$axios.get("mvc/fileParser/getList",{
                }).then((res) => {
                    this.fileParserList = res.data;
                });
            },
            //选中行变更
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },

            // 保存编辑
            saveEdit() {
            alert(1);
                var fileParserIds = this.checked.join(",");
                this.$axios.post(this.addOrupdateUrl, {
                    id: this.id,
                    type: this.form.type,
                    name:this.form.name,
                    fileSuffix: this.form.fileSuffix,
                    fileParserIds: fileParserIds
                }).then((res) => {
                    if(res.data.success) {
                        //this.$set(this.tableData, this.idx, this.form);
                        this.$message.success(`操作成功`);
                        this.editVisible = false;
                        this.getData();
                    } else {
                        this.$message.success(res.data.data);
                    }
                })
            },
            // 确定删除
            deleteRow(){
                this.$axios.post("mvc/fileType/delete", {
                    id: this.id,
                }).then((res) => {
                    if (res.data.success) {
                        this.$message.success("删除成功");
                    } else {
                        this.$message.error(res.data.data);
                    }
                    this.delVisible = false;
                    this.getData();
                })

            }
        }
    }

</script>

<style scoped>
    .handle-box {
        margin-bottom: 20px;
    }

    .handle-select {
        width: 120px;
    }

    .handle-input {
        width: 300px;
        display: inline-block;
    }
    .del-dialog-cnt{
        font-size: 16px;
        text-align: center
    }
    .table{
        width: 100%;
        font-size: 14px;
    }
    .red{
        color: #ff0000;
    }
</style>
