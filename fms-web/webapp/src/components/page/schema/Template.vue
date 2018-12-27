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

        <el-dialog title="新增" :visible.sync="addVisible" width="740px">
            <el-form ref="form"  :label-position="labelPosition" label-width="100px">
                <!--<el-form-item label="解析器：" prop="parser">-->
                    <!--<Select v-model="form.parser" filterable>-->
                        <!--&lt;!&ndash;@on-change="getParser"&ndash;&gt;-->
                        <!--<Option v-for="item in parserData" :value="item.id" :key="item.id">{{ item.name }}</Option>-->
                    <!--</Select>-->
                <!--</el-form-item>-->
                <el-form-item label="模板名称：" prop="templateName">
                    <el-input v-model="formList[0].templateName" style="width: 180px"></el-input>
                </el-form-item>
                <Form inline v-for="(item,index) in formList">
                    <FormItem label="key：">
                        <el-input v-model="formList[index].columnKey" style="width: 130px"></el-input>
                    </FormItem>
                    <FormItem label="库名：" >
                        <Select @on-change="(schemaId) => getTables(schemaId,index)" v-model="formList[index].schemaId" filterable style="width: 130px">
                            <Option v-for="(schema,schemaIdx) in schemas" :value="schema.id" :key="schemaIdx"> {{ schema.name }}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="表名：">
                        <Select @on-change="(tableId) => getColumnsByTable(tableId,index)" v-model="formList[index].tableId" filterable style="width: 130px">
                            <Option v-for="(table,tableIdx) in selectMap[index].tables" :value="table.id" :key="table.id"> {{ table.tableChinese }}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="字段名：">
                        <Select @on-change="(columnId) => getDicByColumn(columnId,index)" v-model="formList[index].columnId" filterable style="width: 130px">
                            <Option v-for="(column,columnIdx) in selectMap[index].columns" :value="column.id" :key="column.id"> {{ column.columnChinese }}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="操作：">
                        <i @click="rowPlus()" v-if="index==0" class="el-icon-circle-plus-outline" style="font-size: 30px;cursor: pointer"></i>
                        <i @click="rowRemove(index)" v-if="index!=0" class="el-icon-remove-outline" style="font-size: 30px;cursor: pointer"></i>
                    </FormItem>
                </Form>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdd">确 定</el-button>
            </span>
        </el-dialog>



        <el-dialog title="编辑" :visible.sync="editVisible" width="740px"  >
            <el-form ref="form"  :label-position="labelPosition" label-width="100px">
            <el-form-item label="模板名称：" prop="templateName">
                <el-input v-model="formList2[0].templateName" style="width: 180px"></el-input>
            </el-form-item>
            <Form inline v-for="(item,index) in formList2">
                <FormItem label="key：">
                    <el-input v-model="formList2[index].columnKey" style="width: 130px"></el-input>
                </FormItem>
                <FormItem label="库名：" >
                    <Select @on-change="(schemaId) => getTables(schemaId,index)" v-model="formList2[index].schemaId" filterable style="width: 130px">
                        <Option v-for="(schema,schemaIdx) in schemas" :value="schema.id" :key="schemaIdx"> {{ schema.name }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="表名：">
                    <Select @on-change="(tableId) => getColumnsByTable(tableId,index)" v-model="formList2[index].tableId" filterable style="width: 130px">
                        <Option v-for="(table,tableIdx) in selectMap[index].tables" :value="table.id" :key="table.id"> {{ table.tableChinese }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="字段名：">
                    <Select @on-change="(columnId) => getDicByColumn(columnId,index)" v-model="formList2[index].columnId" filterable style="width: 130px">
                        <Option v-for="(column,columnIdx) in selectMap[index].columns" :value="column.id" :key="column.id"> {{ column.columnChinese }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="操作：">
                    <i @click="rowPlus()" v-if="index==0" class="el-icon-circle-plus-outline" style="font-size: 30px;cursor: pointer"></i>
                    <i @click="rowRemove(index)" v-if="index!=0" class="el-icon-remove-outline" style="font-size: 30px;cursor: pointer"></i>
                </FormItem>
            </Form>
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
                labelPosition:"left",
                editVisible: false,
                delVisible: false,
                pageSize: 10,
                currentPage: 1,
                tableData: [],
                addVisible: false,
                formList:[{
                    templateName:'',
                    columnKey: '',
                    schemaId: '',
                    tableId: '',
                    columnId: ''
                }],
                formList2:[{
                    templateName:'',
                    columnKey: '',
                    schemaId: '',
                    tableId: '',
                    columnId: ''
                }],
                schemas: [],
                selectMap:[{
                    tables: [],
                    columns: []
                }],
                parserData: {
                    type: Array,
                    default: []
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
        mounted() {
            this.getParsers()
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
                // if (!this.form.key || !this.form.column || !this.form.table || !this.form.schema|| !this.form.parser) {
                //     this.$message.warning('请填写完整表单')
                //     return
                // }
                for(var i in this.formList){
                    this.formList[i].templateName=this.formList[0].templateName
                }
                console.log(this.formList)
                await this.$axios.post('mvc/template/saveTemplate', {
                    formList:JSON.stringify(this.formList)
                    // parser:this.form.parser,
                    // key:this.form.key,
                    // schema:this.form.schema,
                    // table:this.form.table,
                    // column:this.form.column
                });
                await this.getData();
                this.addVisible = false;
            },


            async submitEdit() {

            for(var i in this.formList2){
                this.formList2[i].templateName=this.formList2[0].templateName
            }
            console.log(this.formList2)
            await this.$axios.post('mvc/template/editTemplate', {
                    formList2:JSON.stringify(this.formList2)
        });
        await this.getData();
        this.addVisible = false;
    },


            handleAdd() {
                this.getSchemas();
                this.formList=[{
                    templateName:'',
                    columnKey: '',
                    schemaId: '',
                    tableId: '',
                    columnId: ''
                }],
                this.selectMap=[{
                    tables: [],
                    columns: []
                }],
                this.addVisible = true;
            },
            rowPlus(index){
                console.log(this.formList);
                this.selectMap[this.formList.length]={
                    tables: [],
                    columns: []
                };
                this.formList.push({
                    templateName:'',
                    columnKey: '',
                    schemaId: '',
                    tableId: '',
                    columnId: ''
                });
            },
            rowRemove(index){
                this.formList.splice(index,1)
                this.selectMap.splice(index,1)
            },
            handleDelete(index, row) {
                this.idx = index;
                this.delVisible = true;
            },

            // 确定删除
            async deleteRow() {
                await this.$axios.post('mvc/template/deleteTemplate', {id: this.tableData[this.idx].id});
                await this.getData();
                this.$message.success('删除成功');
                this.delVisible = false;
            },

            handleCurrentChange(val) {
                this.currentPage = val;
            },
            //解析器下拉列表
            getParsers() {
                this.$axios.post('mvc/fileParser/getOrderList', {}).then(res => {
                    this.parserData = res.data ? res.data : [];
                });
            },
            //获取库
            getSchemas() {
                this.$axios.post('mvc/getAllSchemas').then(res => {
                    this.schemas = res.data
                })
            },
            //根据库ID获取表
            getTables(schemaId,idx) {
                this.$axios.post('mvc/getTablesBySchemaId', {
                    schemaId: schemaId
                }).then(res => {
                    this.$set(this.selectMap[idx], 'tables', res.data);
                    this.$set(this.selectMap[idx], 'columns', '')
                    this.$set(this.formList[idx], 'tableId', '')
                    this.$set(this.formList[idx], 'columnId', '')
                    console.log(this.selectMap)
                })
            },
            //根据表ID获取字段
            getColumnsByTable(tableId,idx) {
                this.$axios.post('mvc/getColumnsForTable', {tableId}).then(res => {
                    this.$set(this.selectMap[idx], 'columns', res.data)
                    this.$set(this.formList[idx], 'columnId', '')
                    console.log(this.selectMap)
                })
            },
            getDicByColumn(columnId, key) {
                // const column = this.columnSelectMap[key]['columns'].find(c => c.id === columnId)
                // console.log(column)
                // const {isDic, tableId} = column || {}
                //     this.getDicByTableId(tableId, key)
            },
        }
    }
</script>
