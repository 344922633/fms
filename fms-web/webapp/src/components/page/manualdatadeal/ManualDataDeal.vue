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

        <Layout>
            <!--目录树-->
            <Sider width="200" style="background-color: #fff" >
                <Card title="目录">
                    <div class="tree">
                        <el-tree :data="treedata" :props="defaultProps" @node-click="getColumns"></el-tree>
                    </div>
                </Card>
            </Sider>
            <Layout>

                <Divider>字段信息()</Divider>
                <Form v-if="tableColumns" :label-width="100">
                    <FormItem v-for="(item, index) in tableColumns" :data="item.column.dicTableName" :key="item.column.id" :label="item.column.columnChinese">



                       <!-- 当item.column.isDic === 0 时判断    item.column.columnEnglish.toLowerCase() == 'dxbm'     成立  则生成时间戳赋值给dataValue   只读-->
                        <Input  v-if="item.column.isDic === 0 && item.column.columnEnglish.toLowerCase() != 'dxbm'" :type="checkType(item.column.dataType)"  v-model="item.column.dataValue" :placeholder="item.column.dataType" >
                        </Input>


                        <Input  v-if="item.column.isDic === 0 && item.column.columnEnglish.toLowerCase() == 'dxbm'" v-model="item.column.dataValue" disabled="disabled">
                        </Input>
                        <Select v-if="item.column.isDic === 1" v-model="item.column.dicList" filterable>
                            <Option :value="singlevalue.MC" v-for="singlevalue in item.dicList" >{{singlevalue.MC}}</option>
                        </Select>

                    </FormItem>
                    <Button @click="handleSave">保存</Button>
                </Form>
            </Layout>
        </Layout>
    </div>
</template>
<script>

    export default {
        data() {
            return {
                table_name: '',
                masterslave_name: '',
                tableName: '',
                tableNames: [],
                masterslaveList: [],
                tableColumns:null,
                treedata: [],
                timedata:'',
                defaultProps: {
                    children: 'children',
                    label: 'name'
                }
            }
        },
        created() {
            //this.getTables();
            this.getMenuListFormasterslave();
            this.timedata=Date();
        },
        methods: {
            checkType(type){
                console.log(type == "varchar" || type == "char",type + "....");
                if(type == "varchar" || type == "char"){
                    return "text";
                }else if(type == "int" || type == "long"){
                    return "number";
                }else if(type == "time"){
                    return "date";
                }
            },
            getTables() {
                // this.$axios.post('mvc/getTables').then(res => {
                //    this.tableNames = res.data;
                // })
                this.$axios.post('mvc/getMasterSlaveList').then(res => {
                    this.masterslaveList = res.data;
                })

            },
            getMenuListFormasterslave() {
                this.$axios.post('mvc/getMenuListFormasterslave').then(res => {
                    this.treedata = res.data;
                })

            },
            getColumns(data) {
                console.warn(data) ;
                if(data  != null && (data.children == null ||  data.children.length() == 0)){
                    var masterslaveview_id = data.id;
                    var timestamp = (new Date()).valueOf();
                    this.$axios.post('mvc/listColumnsFormasterslave', {
                        masterSlaveId: masterslaveview_id
                    }).then(res => {
                        this.tableColumns = res.data;
                        console.log(res.data, '返回数据');
                        //alert(JSON.stringify(this.tableColumns));
                        console.log(this.tableColumns);
                        this.tableColumns.forEach((item, index) => {
                            if(item.column_key=="PRI" ){
                                var val=this.tableColumns[index].placeholder;
                                val =timestamp;


                                this.tableColumns[index].readonly = true;
                                this.tableColumns[index].value=val;
                            }else if(item.column.columnEnglish.toLowerCase() == 'dxbm'){
                                item.column.dataValue = (new Date()).valueOf()+"";
                            }
                        })
                        console.log(this.tableColumns);
                  /*      this.masterslave_name = masterslaveview_name;*/
                    })
                }
            },
            handleSave() {
                this.$axios.post('mvc/formEntrySendKafka', {

                    data: JSON.stringify(this.tableColumns)
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
