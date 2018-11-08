<template>
    <div>
        <Form :label-width="100">
            <Divider>表信息</Divider>
            <FormItem label="选择表">
                <Select v-model="masterslave_name" @on-change="getColumns" >
                    <Option v-for="item in masterslaveList" :value="item.name" :key="item.name">{{ item.name }}</Option>
                </Select>
            </FormItem>
            <Divider>字段信息</Divider>
            <FormItem v-for="(item, index) in tableColumns" :key="item.column_name" :label="item.column_desc">
                <Input v-model="item.value"/>
            </FormItem>
            <Divider>操作</Divider>
            <Button @click="handleSave">保存</Button>
        </Form>
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
                tableColumns: []
            }
        },
        created() {
            this.getTables();
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
            getColumns(masterslave_name) {
                this.$axios.post('mvc/listColumnsFormasterslave', {
                    masterslavename: masterslave_name
                }).then(res => {
                    this.tableColumns = res.data;
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
