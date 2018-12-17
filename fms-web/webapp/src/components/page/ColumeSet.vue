<style scoped>
    .tree {
        overflow-y: auto;
        overflow-x: scroll;
        /*width: 240px;*/
        height: 463px;
        background-color: #ffffff;
    }
    .el-tree {
        min-width: 100%;
        font-size: 14px;
        display: inline-block !important;
    }
    .ivu-card-body {
        padding: 5px;
    }
    .ivu-spin-fix {
        top: 30px;
    }
    .demo-spin-icon-load{
        animation: ani-demo-spin 1s linear infinite;
    }
</style>
<template>
    <div class="layout">

  <!--   <Layout>
        &lt;!&ndash;目录树&ndash;&gt;
                    <Sider width="200" style="background-color: #fff" >
                        <Card title="目录">
                            <div class="tree">
                                <el-tree :data="treedata" :props="defaultProps" @node-click="getColumns"></el-tree>
                            </div>
                        </Card>
                    </Sider>-->
                  <Layout>
        <Form :label-width="100">
            <FormItem label="选择表">
                <Select filterable v-model="table_name" @on-change="getColumns" style="width:300px;">
                    <Option v-for="item in tableNames" :value="item" :key="item">{{ item }}</Option>
                </Select>
            </FormItem>
            <Divider>字段信息({{table_name}})<Input v-model="tableChinese" style="margin-left: 28px;"/>
            </Divider>
            <FormItem v-for="(item, index) in tableColumns" :key="item.column_name" :label="item.column_name">
                <!--<Input v-if="item.inputType != 'select' && !(item.max_length > 100) " v-model="item.value" :placeholder="item.data_type" :readonly="item.readonly" :value="item.value" style="width:300px;"/>-->
                <Input v-model="item.value" :placeholder="item.data_type" :readonly="item.readonly" :value="item.value" style="width:300px;"/>
                <Select v-if="checkedArr[index]" filterable v-model="dicName" style="width:300px;">
                    <Option v-for="item in dicTableNames" :value="item" :key="item">{{ item }}</Option>
                </Select>
                <input type="checkbox" v-model="checkedArr1[index]" value="是否为主键" id='masterKey' style="margin-left: 8px;">
                <label >是否为主键</label>
                <input type="checkbox" v-model="checkedArr[index]" value="是否为字典表字段" id='dicColumn' style="margin-left: 28px;">
                <label >是否为字典表字段</label>
                <Input type="textarea" v-if="item.inputType != 'select' && item.max_length > 100" v-model="item.value" :placeholder="item.data_type" style="width:300px;"/>
            </FormItem>
            <Button @click="handleSave">保存</Button>
        </Form>
         </Layout>
    </div>
</template>
<script>

    export default {
        data() {
            return {
                dicName: '',
                table_name: '',
                tableName: '',
                tableNames: [],
                dicTableNames: [],

                // masterslaveList: [],
                tableColumns:[],
                treedata: [],
                defaultProps: {
                  children: 'children',
                  label: 'name'
                },
                checkedArr: [],
                checkedArr1: []

            }
        },
        created() {
            this.getTables();
            this.getDicTables();

        },
        watch: {
            checkedArr(newArr) {
                // console.log(newArr)
            }
        },
        methods: {
            getColumns(data) {
            this.table_name = data;
            // var timestamp = (new Date()).valueOf();
                this.$axios.post('mvc/listColumnsForTable', {
                    tableName: data
                }).then(res => {
                    // console.log(res.data);
                    this.tableColumns = res.data;
                    res.data.forEach((val, idx) => {
                        this.$set(this.checkedArr, idx, false)
                        this.$set(this.checkedArr1, idx, false)

                    })
                    //alert(JSON.stringify(this.tableColumns));
                    this.tableColumns.forEach((item, index) => {
                    if(item.column_key=="PRI" ){
                         var val=this.tableColumns[index].placeholder;
                                 val =timestamp;

                                 this.tableColumns[index].readonly = true;
                                  this.tableColumns[index].value=val;

                    }else{

                    }
                    })
                })
            },

            getTables() {
                this.$axios.post('mvc/getAllNzList').then(res => {
                    this.tableNames = res.data;
                })
            },

            getDicTables() {
                this.$axios.post('mvc/getDicTable').then(res => {
                    this.dicTableNames = res.data;
                    console.log(res.data);
                })
            },
            handleSave() {
                this.$axios.post('mvc/insertDataForTableInfo', {
                    tableEnglish: this.table_name,
                    tableChinese:this.tableChinese,
                    data: JSON.stringify(this.tableColumns),
                    checkedArr:this.checkedArr,
                    checkedArr1:this.checkedArr1

                }).then(res => {
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                })
            }
        }
    }
</script>
