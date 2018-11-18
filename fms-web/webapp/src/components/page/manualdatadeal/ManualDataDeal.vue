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
        <Form :label-width="100">
            <Divider>字段信息({{masterslave_name}})</Divider>
            <FormItem v-for="(item, index) in tableColumns" :key="item.column_name" :label="item.column_desc">
                <Input v-if="item.inputType != 'select'" v-model="item.value" v-validate="item.validate"/>
                <Select v-if="item.inputType == 'select'" v-model="item.value" >
                    <Option :value="singlevalue.selectValue" v-for="singlevalue in item.selectvalue" >{{singlevalue.selectLable}}</option>
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
                tableColumns:[],
                treedata: [],
                defaultProps: {
                  children: 'children',
                  label: 'name'
                }
            }
        },
        created() {
            //this.getTables();
            this.getMenuListFormasterslave();
        },
        methods: {
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
            var masterslaveview_name = data.name;
                this.$axios.post('mvc/listColumnsFormasterslave', {
                    masterslavename: masterslaveview_name
                }).then(res => {
                    this.tableColumns = res.data;
                    this.masterslave_name = masterslaveview_name;
                })
            },
            handleSave() {
                this.$axios.post('mvc/insertDataFormasterslave', {
                    masterslavename: this.masterslave_name,
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
