<template>
    <div class="container">
        <Button @click="handleAdd">新增</Button>
        <el-table :data="tableData.slice((currentPage-1)*pageSize,currentPage*pageSize)" border class="table"

                  ref="multipleTable">
            <el-table-column prop="tableEnglish" align="center" label="表名" width="120">
            </el-table-column>
            <el-table-column prop="tableChinese" align="center" label="表名映射" width="120">
            </el-table-column>
            <el-table-column label="操作" align="center" width="300">
                <template slot-scope="scope">
                    <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑
                    </el-button>
                    <el-button type="text" icon="el-icon-delete" class="red"
                               @click="handleDelete(scope.$index, scope.row)">删除
                    </el-button>
                    <el-button type="text" icon="el-icon-caret-right" class="red"
                               @click="getColumns(scope.$index, scope.row)">字段映射
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新增" :visible.sync="addVisible" width="40%">
            <Form :label-width="100">
                <FormItem label="选择表">
                    <Select v-model="table_name" style="width:300px;">
                        <Option v-for="item in tableNames" :value="item" :key="item">{{ item }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="表格映射">
                    <Input v-model="tableChinese" style="width:300px;" />
                </FormItem>
            </Form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdd">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="字段映射" :visible.sync="columnSetVisible" width="80%">
            <h1></h1>
            <Form :label-width="100">
                <Divider>字段信息({{tableMapItem.tableChinese}})
                </Divider>
                <FormItem v-for="(item, index) in tableColumns" :key="item.columnEnglish" :label="item.columnEnglish">

                    <!--&lt;!&ndash;DXBM字段显示时间戳  只读&ndash;&gt;    :type="checkType(item.dataType)"                      -->

                    <!--<Input v-model="item.columnChinese" v-if="item.columnEnglish.toLowerCase() != 'dxbm'" :placeholder="item.dataType"  :value="item.columnChinese" style="width:300px;"/>-->

                    <!--<Input v-model="item.columnChinese" v-if="item.columnEnglish.toLowerCase() == 'dxbm'" disabled="disabled" :value="item.columnChinese" style="width:300px;"/>-->
                    <Input v-model="item.columnChinese" :placeholder="item.data_type" :value="item.columnChinese" style="width:300px;"/>

                    <Select v-if="checkedArr[index]" v-model="item.dicTableName" style="width:300px;">
                        <Option v-for="item in dicTableName" :value="item" :key="item">{{ item }}</Option>
                    </Select>

                    <input type="checkbox" v-model="checkedArr1[index]" value="是否为主键" id='masterKey' style="margin-left: 8px;">
                    <label >是否为主键</label>
                    <input type="checkbox" v-model="checkedArr[index]" value="是否为字典表字段" id='dicColumn' style="margin-left: 28px;">
                    <label >是否为字典表字段</label>
                    <!--        <Input type="textarea" v-if="item.inputType != 'select' && item.max_length > 100" v-model="item.value" :placeholder="item.data_type" style="width:300px;"/>-->
                </FormItem>
            </Form>

            <span slot="footer" class="dialog-footer">
                <el-button @click="columnSetVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveMapEdit">确 定</el-button>
            </span>


        </el-dialog>

        <el-dialog title="编辑" :visible.sync="editVisible" width="40%">
            <Form :label-width="100">
                <FormItem label="选择表">
                    <Select v-model="tableNameEdit" style="width:300px;">
                        <Option v-for="item in tableNames" :value="item" :key="item">{{ item }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="表格映射">
                    <Input v-model="tableChineseEdit" style="width:300px;" />
                </FormItem>
            </Form>
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
                table_name: '',
                tableChinese: '',
                dataType: '',
                tableNameEdit: '',
                tableMapItem: {},
                tableColumns: [],
                dicName:'',
                checkedArr: [],
                checkedArr1: [],
                dicTableName: [],
                tableChineseEdit: '',
                labelPosition:"left",
                tableNames: [],
                addVisible: false,
                editVisible: false,
                delVisible: false,
                pageSize: 10,
                currentPage: 1,
                tableData: [],

                idx: -1,
                columnSetVisible:false
            }
        },

        created() {
            this.getData();
            this.getDicTables();
        },
        methods: {
            // checkType(type){
            //     if(type == "varchar" || type == "char"){
            //         return "test";
            //     }else{
            //         return "number";
            //     }
            // },
        async getTables() {
        return this.$axios.post('mvc/getAllXxList').then(res => {
            this.tableNames = res.data;
            })
            },

    getDicTables() {
        this.$axios.post('mvc/getXxDicTable').then(res => {
            this.dicTableName = res.data;
    })
    },

    async getData() {
        let {data} = await this.$axios.post('mvc/getAllTables');
        this.tableData = data;
    },


    async handleEdit(index, row) {
        const {tableChinese, tableEnglish, id} = row
        this.idx = index;
        const item = this.tableData[index];
        await this.getTables();
        this.editVisible = true;
        this.tableChineseEdit = tableChinese
        this.tableNameEdit = tableEnglish
        this.tableIdEdit = id
    },

    async submitAdd() {
        this.getTables();
        this.addVisible = false;
        await this.$axios.post('mvc/addTableInfo', {
            tableEnglish: this.table_name,
            tableChinese:this.tableChinese

        }).then(function(res){
            if(!res.data.success){
                alert(res.data.data);
            }
        });
        await this.getData();

    },

    async submitEdit() {
        await this.$axios.post('mvc/updateTableInfo', {
            tableId: this.tableIdEdit,
            tableEnglish: this.tableNameEdit,
            tableChinese:this.tableChineseEdit
        });
        await this.getData();
        this.editVisible = false;
    },


    async handleAdd() {
        this.table_name="";
        this.tableChinese="";
        this.getTables();
        this.addVisible = true;
    },

    async handleDelete(index, row) {
        this.idx = index;
        this.delVisible = true;
    },

    // 确定删除
    async deleteRow() {
        let index = this.pageSize * (this.currentPage - 1) + this.idx;
        this.delVisible = false;
        await this.$axios.post('mvc/deleteTableInfo', {
            id: this.tableData[index].id});
        await this.getData();
        this.$message.success('删除成功');
        this.delVisible = false;
    },

    handleCurrentChange(val) {
        this.currentPage = val;
    },

    getColumns(index, row) {
        this.tableMapItem = row
        this.$axios.post('mvc/getColumnsInfo', {
            id: this.tableData[(this.currentPage-1)*this.pageSize + index].id,

            tableName: row.tableEnglish
        }).then(res => {
            this.tableColumns = res.data;
        res.data.forEach((val, idx) => {
            this.$set(this.checkedArr, idx, !!val.isDic)
        this.$set(this.checkedArr1, idx, !!val.isMasterKey)
    })
    })
        this.columnSetVisible=true;
    },
    saveMapEdit() {
        const {tableChinese, tableEnglish, id} = this.tableMapItem
        this.tableColumns.forEach((column, idx) => {
           if(this.checkedArr[idx]){
                column.isDic =1
            }else{
                column.isDic =0
                column.dicTableName = null;
            };
            if(this.checkedArr1[idx]){
                column.isMasterKey = 1
            }else{
                column.isMasterKey = 0
            }

           /* column.isDic = this.checkedArr[idx]
            column.isMasterKey = this.checkedArr1[idx]*/
    })
        this.$axios.post('mvc/saveColumnInfos', {
            tableId: id,
            tableEnglish:tableEnglish,
            tableChinese:tableChinese,
            data: JSON.stringify(this.tableColumns),
        }).then(res => {
            this.$notify({
            title: '提示',
            message: res.data.data,
            type: res.data.success ? 'success' : 'error'
        });
    })
        this.columnSetVisible=false;
    }

    }
    }
</script>
