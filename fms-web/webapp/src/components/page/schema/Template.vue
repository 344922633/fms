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
            <!--<el-table-column prop="parserName" align="center" label="解析器" width="120">
            </el-table-column>-->
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
                    <template v-if="selectMap[index] && selectMap[index].dicTables">
                        <FormItem label=" ">
                            <div style="margin-top:30px">==></div>
                        </FormItem>
                        <FormItem v-for="dicTable in selectMap[index].dicTables" :label="dicTable.dicTableName">
                            <Select @on-change="(val)=>{changeColumnDicMap(dicTable,val)}"
                                v-model="formList[index]['dicMap'][dicTable.dicTableName]" filterable style="width: 180px">
                                <Option v-for="(dic,dicIdx) in dicTable.dicList" :value="dic.DM" :key="dic.DM"> {{ dic.MC }}</Option>
                            </Select>
                        </FormItem>
                    </template>
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
                    <el-input v-model="item.columnKey" style="width: 130px"></el-input>
                </FormItem>
                <FormItem label="库名：" >
                    <Select @on-change="(schemaId) => getTables(schemaId,index)" v-model="item.schemaId" filterable style="width: 130px">
                        <Option v-for="(schema,schemaIdx) in schemas" :value="schema.id" :key="schemaIdx"> {{ schema.name }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="表名：">
                    <Select @on-change="(tableId) => getColumnsByTable(tableId,index)" v-model="formList2[index].tableId" filterable style="width: 130px">
                        <Option v-for="(table,tableIdx) in selectMap[index].tables" :value="table.tableId" :key="table.tableId"> {{ table.tableChinese }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="字段名：">
                    <Select @on-change="(columnId) => getDicByColumn(columnId,index)" v-model="formList2[index].columnId" filterable style="width: 130px">
                        <Option v-for="(column,columnIdx) in selectMap[index].columns" :data="column.id" :value="column.id" :key="column.id"> {{ column.columnChinese }}</Option>
                    </Select>
                </FormItem>
                <template v-if="selectMap[index] && selectMap[index].dicTables">
                    <FormItem label=" ">
                        <div style="margin-top:30px">==></div>
                    </FormItem>
                    <FormItem v-for="dicTable in selectMap[index].dicTables" :label="dicTable.dicTableName">
                        <Select @on-change="(val)=>{changeColumnDicMap(dicTable,val)}"
                                v-model="formList2[index]['dicMap'][dicTable.dicTableName]"  filterable style="width: 180px">

                           <!-- labelPosition -->
                            <Option v-for="(dic,dicIdx) in dicTable.dicList" :value="dic.DM+''"  :key="dic.DM"> {{ dic.MC }}</Option>
                        </Select>
                    </FormItem>
                </template>
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
                    columnId: '',
                    dicMap:{}
                }],
               formList2:[{
                    templateName:'',
                    columnKey: '',
                    schemaId: '',
                    tableId: '',
                    columnId: '',
                    dicMap:{}
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

            async handleEdit(index, row) {
                this.idx = index;
                this.getSchemas();
                let templateData = await this.$axios.get('mvc/template/findAllByTemplate', {
                    params: {
                        templateName:row.templateName
                    }
                });
                let list = templateData.data;
                console.log(list);
                this.formList2 = list;
                for(let i = 0, arr = list.length; i < arr; i++) {
                    console.log(list[i])
                    this.$set(this.selectMap, i, {
                        tables: [],
                        columns: []
                    });
                    this.getTables(list[i].schemaId, i);
                    this.getColumnsByTable(list[i].tableId, i)
                }


                //this.getDicByTableId(row.tableId, 0);
        console.log(this.selectMap,'编辑');

               console.log(this.formList2,'扯淡')
                //const item = this.tableData[index];
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

                for(var i in this.formList){
                    this.formList[i].templateName=this.formList[0].templateName
                }
                console.log(this.formList, '新增提交数据')
                this.addVisible = false;
                await this.$axios.post('mvc/addColumnMapRelations', {
                    formList:JSON.stringify(this.formList)
                }).then( (result) => {
            console.log(result, '成功')
        this.$message.success('提交成功')

    });
                await this.getData();

            },

            async submitEdit() {
            for(var i in this.formList2){
                this.formList2[i].templateName=this.formList2[0].templateName
           }
            console.log(this.formList2,'编辑')
            this.editVisible = false;
            await this.$axios.post('mvc/addColumnMapRelations', {
            formList:JSON.stringify(this.formList2)
        }).then( (result) => {
            console.log(result, '成功')
            this.$message.success('提交成功')


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
                    columnId: '',
                    dicMap:{}
                }],
                this.selectMap=[{
                    tables: [],
                    columns: []
                }],
                this.addVisible = true;
            },
            rowPlus(index){
                console.log(this.formList);
                this.selectMap[this.formList.length]={};
                this.formList.push({
                    templateName:'',
                    columnKey: '',
                    schemaId: '',
                    tableId: '',
                    columnId: '',
                    dicMap:{}
                })
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
                let index = this.pageSize * (this.currentPage - 1) + this.idx;
                console.log(this.pageSize, this.currentPage, this.idx)
                this.delVisible = false;
                await this.$axios.post('mvc/template/deleteTemplate', {id: this.tableData[index].id});
                await this.getData();
                this.$message.success('删除成功');

            },

            handleCurrentChange(val) {
                this.currentPage = val;
            },
            //refresh
            refresh(){
                for (let i = 0; i < this.selectMap.length; i++) {
                    this.selectMap[i] = Object.assign({}, this.selectMap[i], { fresh: new Date().getTime()});
                }
                this.selectMap = Object.assign({}, this.selectMap);
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
                    this.$set(this.selectMap[idx], 'tables', res.data)
                    console.log(this.selectMap)
                    //this.$set(this.formList[idx], 'tableId', '')
                   // this.$set(this.formList[idx], 'columnId', '')
                    //this.$set(this.selectMap[idx], 'columns', null)
                    //this.$set(this.selectMap[idx], 'dicTables', null)
                    //this.$set(this.formList[idx], 'dicMap', {})
                    this.refresh()
                })
            },
            changeColumnDicMap(mapName, val) {
                for (let i in this.formList) {
                    let cur = this.formList[i];
                    console.log(mapName, val, '选择参数值')
                    cur.dicMap[mapName] = val
                    this.$set(cur, 'dicMap', cur.dicMap)
                }
                //this.$set(cur, dicMap, cur.dicMap)
            },
            //根据表ID获取字段
            getColumnsByTable(tableId,idx) {
                console.log(tableId, 'tableId')
                this.$axios.post('mvc/getColumnsForTable', {tableId}).then(res => {
                    this.$set(this.selectMap[idx], 'columns', res.data)
                    //this.$set(this.formList[idx], 'columnId', '')
                    //this.$set(this.selectMap[idx], 'dicTables', null)
                    //this.$set(this.formList[idx], 'dicMap', {})
                    this.refresh()

                    this.getDicByTableId(tableId, idx)
                })
            },
            getDicByColumn(columnId, key) {
                // const column = this.columnSelectMap[key]['columns'].find(c => c.id === columnId)
                // console.log(column)
                // const {isDic, tableId} = column || {}
                //     this.getDicByTableId(tableId, key)
            },
           getDicByTableId(tableId, key) {
                this.$axios.post('mvc/getDicNameByTableId', {
                    tableId
                }).then(res => {
                    const dicTables = res.data || []
                    this.$set(this.selectMap[key], 'dicTables', dicTables);
                    let dicMap = null
                    for (let i in this.formList) {
                        let cur = this.formList[i]
                        if (cur.dicMap && Object.keys(cur.dicMap).length) {
                            dicMap = cur.dicMap
                        }
                    }
                    this.$set(this.formList[key], 'dicMap', dicMap || {})

                    dicTables.forEach(dicTable => {
                        const { columnEnglish } = dicTable
                        this.$set(this.formList[key]['dicMap'], columnEnglish, '')
                    })
                   // this.getDicColumnsByDicName(dicTable,key);
                })
            },
            /*getDicColumnsByDicName(dicTable, key) {
                this.$axios.post('mvc/getDicColumnsByDicName', {
                    dicName:dicTable
                }).then(res => {
                    thigetDicNameByTableIds.$set(this.selectMap[key], 'dicColumns', res.data)
                    console.log(res.data)
                });
            },*/
        }
    }
</script>
