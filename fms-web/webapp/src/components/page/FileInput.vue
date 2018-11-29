<template>
    <div class="container">
        <Button @click="handleAdd">新增</Button>
        <el-table :data="tableData.slice((currentPage-1)*pageSize,currentPage*pageSize)" border class="table"
                  ref="multipleTable">
            <el-table-column prop="ip" align="center" label="ip" width="120">
            </el-table-column>
            <el-table-column prop="userName" align="center" label="用户名" width="120">
            </el-table-column>
            <el-table-column prop="password" align="center" label="密码" width="120">
            </el-table-column>
            <el-table-column prop="port" align="center" label="端口号" width="120">
            </el-table-column>
            <el-table-column prop="path" align="center" label="下载路径" width="120">
            </el-table-column>
            <el-table-column prop="format" align="center" label="是否格式化" width="120">
            </el-table-column>
            <el-table-column label="操作" align="center" width="160">
                <template slot-scope="scope">
                    <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑
                    </el-button>
                    <el-button type="text" icon="el-icon-delete" class="red"
                               @click="handleDelete(scope.$index, scope.row)">删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新增" :visible.sync="addVisible" width="40%">
            <el-form ref="form" :model="form" label-width="100px" :rules="rules" @click="onSubmitAdd">
                <el-form-item label="ip：" prop="ip">
                    <el-input v-model="form.ip"></el-input>
                </el-form-item>
                <el-form-item label="用户名：" prop="userName">
                    <el-input v-model="form.userName"></el-input>
                </el-form-item>
                <el-form-item label="密码：" prop="password">
                    <el-input v-model="form.password"></el-input>
                </el-form-item>
                <el-form-item label="端口号：" prop="port">
                    <el-input v-model="form.port"></el-input>
                </el-form-item>
                <el-form-item label="下载路径：" prop="path">
                    <el-input v-model="form.path"></el-input>
                </el-form-item>
                <el-form-item label="是否格式化：" prop="selects">
                    <el-select v-model="form.format" placeholder="请选择" class="handle-select mr10">
                        <el-option key="1" label="格式化" value="格式化"></el-option>
                        <el-option key="2" label="非格式化" value="非格式化"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdd">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="编辑" :visible.sync="editVisible" width="40%" :rules="rules">
            <el-form ref="form" :model="form" label-width="100px">
                <el-form-item label="ip：" prop="ip">
                    <el-input v-model="form.ip"></el-input>
                </el-form-item>
                <el-form-item label="用户名：" prop="userName">
                    <el-input v-model="form.userName"></el-input>
                </el-form-item>
                <el-form-item label="密码：" prop="password">
                    <el-input v-model="form.password"></el-input>
                </el-form-item>
                <el-form-item label="端口号：" prop="port">
                    <el-input v-model="form.port"></el-input>
                </el-form-item>
                <el-form-item label="下载路径：" prop="path">
                    <el-input v-model="form.path"></el-input>
                </el-form-item>
                <el-form-item label="是否格式化：" prop="selects">
                    <el-select v-model="form.format" placeholder="请选择" class="handle-select mr10">
                        <el-option key="1" label="格式化" value="格式化"></el-option>
                        <el-option key="2" label="非格式化" value="非格式化"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitEdit">确 定</el-button>
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

        <div class="pagination">
            <el-pagination
                background
                layout="prev, pager, next"
                :total="tableData.length"
                :page-size="pageSize"
                :current-page="currentPage"
                @current-change="handleCurrentChange">
            </el-pagination>
        </div>
    </div>


</template>

<script>
    export default {
        data() {
            return {
                tDirectoryId: 0,//当前选中目录树node节点id
                tDirectoryText: '',//当前选中目录树节点名称

                editVisible: false,
                delVisible: false,
                pageSize: 10,
                currentPage: 1,
                tableData: [],
                addVisible: false,
                form: {
                    ip: '47.93.40.219',
                    userName: 'root',
                    password: 'admin!123',
                    port: '22',
                    path: '/home/xuhubin',
                    format: ''
                },
                idx: -1,
                rules: {
                    ip: [{required: true, message: '请输入ip', trigger: 'blur'}],
                    userName: [{required: true, message: '请输入用户名', trigger: 'blur'}],
                    password: [{required: true, message: '请输入密码', trigger: 'blur'}],
                    port: [{required: true, message: '请输入端口号', trigger: 'blur'}],
                    path: [{required: true, message: '请输入路径', trigger: 'blur'}],
                    format: [{required: true, message: '请选择文件格式', trigger: 'change'}]
                }
            }
        },

        created() {
            this.getData();
        },
        methods: {
            async uploadFile() {
                await
                    this.$axios.post('mvc/uploadFromFtpFile',{
                        ipAddr:this.form.ip,
                        port:this.form.port,
                        userName:this.form.userName,
                        pwd: this.form.password,
                        path: this.form.path,
                    }).then(res => {
                    /*    if(res.data.success){
                            this.$notify({
                                title: '提示',
                                message: '上传成功！',
                                type: 'success'
                            });
                        }else{
                            this.$notify({
                                title: '提示',
                                message: '上传失败！',
                                type: 'error'
                            });
                        }*/
                    }).catch(e => {

                    });
            },

            async uploadFileToLocal() {
                await
                    this.$axios.post('mvc/uploadFileToLocal',{
                        ipAddr:this.form.ip,
                        port:this.form.port,
                        userName:this.form.userName,
                        pwd: this.form.password,
                        path: this.form.path,
                    }).then(res => {
                     /*   if(res.data.success){
                            this.$notify({
                                title: '提示',
                                message: '上传成功！',
                                type: 'success'
                            });
                        }else{
                            this.$notify({
                                title: '提示',
                                message: '上传失败！',
                                type: 'error'
                            });
                        }*/
                    }).catch(e => {

                    });
            },


            /*       // 分页导航
                   handleCurrentChange(val) {
                       this.cur_page = val;
                       this.getData();
                   },*/
            async getData() {
                let {data} = await this.$axios.post('mvc/fileInput/getList');
                this.tableData = data;
            },


            handleEdit(index, row) {
                this.idx = index;
                const item = this.tableData[index];
                this.form = {
                    id: item.id,
                    ip: item.ip,
                    userName: item.userName,
                    password: item.password,
                    port: item.port,
                    path: item.path,
                    format: item.format

                };
                this.editVisible = true;
            },

            async submitAdd() {
                await this.$axios.post('mvc/fileInput/add', {
                    ip: this.form.ip,
                    userName: this.form.userName,
                    password: this.form.password,
                    port: this.form.port,
                    path: this.form.path,
                    format: this.form.format
                });
                await this.getData();
                this.addVisible = false;
                if (this.form.format == "格式化") {
                    await this.uploadFile();
                }
                else{
                    await this.uploadFileToLocal();
                }
            },

            async submitEdit() {
                await this.$axios.post('mvc/fileInput/update', {
                    id: this.form.id,
                    ip: this.form.ip,
                    userName: this.form.userName,
                    password: this.form.password,
                    port: this.form.port,
                    path: this.form.path,
                    format: this.form.format
                });
                await this.getData();
                this.editVisible = false;
                if (this.form.format == "格式化") {
                    await this.uploadFile();
                }
                else{
                    await this.uploadFileToLocal();
                }
            },


            handleAdd() {
                this.form = {
                    id: "",
                    ip: "",
                    userName: "",
                    password: "",
                    port: "",
                    path: "",
                    format: ''
                };
                this.addVisible = true;

            },

            handleDelete(index, row) {
                this.idx = index;
                this.delVisible = true;
            },

            // 确定删除
            async deleteRow() {
                await this.$axios.post('mvc/fileInput/delete', {id: this.tableData[this.idx].id});
                await this.getData();
                this.$message.success('删除成功');
                this.delVisible = false;
            },

            handleCurrentChange(val) {
                this.currentPage = val;
            }
        }
    }
</script>
