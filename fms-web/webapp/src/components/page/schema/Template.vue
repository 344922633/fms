<template>
    <div class="container">
        <Button @click="handleAdd">新增</Button>
        <el-table :data="tableData.slice((currentPage-1)*pageSize,currentPage*pageSize)" border class="table"
                  ref="multipleTable">
            <el-table-column prop="id" align="center" label="id" width="120">
            </el-table-column>
            <el-table-column prop="columnKey" align="center" label="键名" width="120">
            </el-table-column>
            <el-table-column prop="columnName" align="center" label="字段名" width="120">
            </el-table-column>
            <el-table-column prop="tableName" align="center" label="表名" width="120">
            </el-table-column>
            <el-table-column prop="schemaName" align="center" label="库名" width="120">
            </el-table-column>
            <el-table-column prop="parserName" align="center" label="解析器" width="120">
            </el-table-column>
            <el-table-column label="操作" align="center" width="240">
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
      <!--      <el-form ref="form" :model="form"  :label-position="labelPosition" label-width="100px" :rules="rules" @click="onSubmitAdd">
                <el-form-item label="键名：" prop="key">
                    <el-input v-model="form.key"></el-input>
                </el-form-item>
                <el-form-item label="字段名：" prop="column">
                    <el-input v-model="form.column"></el-input>
                </el-form-item>
                <el-form-item label="表名：" prop="table">
                    <el-input v-model="form.table"></el-input>
                </el-form-item>
                <el-form-item label="库名：" prop="schema">
                    <el-input v-model="form.schema"></el-input>
                </el-form-item>
                <el-form-item label="解析器：" prop="parser">
                    <el-input v-model="form.parser"></el-input>
                </el-form-item>
            </el-form>-->
            <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdd">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="编辑" :visible.sync="editVisible" width="40%"  >
       <!--     <el-form ref="form" :model="form" :label-position="labelPosition" label-width="100px" :rules="rules">
                <el-form-item label="键名：" prop="key">
                    <el-input v-model="form.key"></el-input>
                </el-form-item>
                <el-form-item label="字段名：" prop="column">
                    <el-input v-model="form.column"></el-input>
                </el-form-item>
                <el-form-item label="表名：" prop="table">
                    <el-input v-model="form.table"></el-input>
                </el-form-item>
                <el-form-item label="库名：" prop="schema">
                    <el-input v-model="form.schema"></el-input>
                </el-form-item>
                <el-form-item label="解析器：" prop="parser">
                    <el-input v-model="form.parser"></el-input>
                </el-form-item>
            </el-form>-->
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
                labelPosition:"left",
                editVisible: false,
                delVisible: false,
                pageSize: 10,
                currentPage: 1,
                tableData: [],
                addVisible: false,
                form: {
                    key: '',
                    column: '',
                    table: '',
                    schema: '',
                    parser: ''
                },
                idx: -1,
           /*     rules: {
                    ip: [{required: true, message: '请输入ip', trigger: 'blur'}],
                    userName: [{required: true, message: '请输入用户名', trigger: 'blur'}],
                    password: [{required: true, message: '请输入密码', trigger: 'blur'}],
                    port: [{required: true, message: '请输入端口号', trigger: 'blur'}],
                    path: [{required: true, message: '请输入路径', trigger: 'blur'}],
                    format: [{required: true, message: '请选择文件格式', trigger: 'change'}]
                }*/
            }
        },

        created() {
            this.getData();
        },
        methods: {
            async getData() {
                let {data} = await this.$axios.post('mvc/template/getList');
                this.tableData = data;
            },

            handleEdit(index, row) {
                this.idx = index;
                const item = this.tableData[index];
            /*    this.form = {
                    key:item.key,
                    column:item.column,
                    table:item.table,
                    schema:item.schema,
                    parser:item.parser
                };*/
                this.editVisible = true;
            },

            async submitAdd() {
                if (!this.form.key || !this.form.column || !this.form.table || !this.form.schema|| !this.form.parser) {
                    this.$message.warning('请填写完整表单')
                    return
                }

                await this.$axios.post('mvc/fileInput/add', {
                    key:this.form.key,
                    column:this.form.column,
                    table:this.form.table,
                    schema:this.form.schema,
                    parser:this.form.parser

                });
                await this.getData();
                this.addVisible = false;
            },


            async submitEdit() {
                if (!this.form.key || !this.form.column || !this.form.table || !this.form.schema|| !this.form.parser) {
                    this.$message.warning('请填写完整表单')
                    return
                }
                await this.$axios.post('mvc/template/update', {
                    key:this.form.key,
                    column:this.form.column,
                    table:this.form.table,
                    schema:this.form.schema,
                    parser:this.form.parser
                });
                await this.getData();
                this.editVisible = false;
            },


            handleAdd() {
             /*   this.form = {
                    key: "",
                    column: "",
                    table: "",
                    schema: "",
                    parser: ""
                };*/
                this.addVisible = true;

            },

            handleDelete(index, row) {
                this.idx = index;
                this.delVisible = true;
            },

            // 确定删除
            async deleteRow() {
                await this.$axios.post('mvc/template/delete', {id: this.tableData[this.idx].id});
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
