<template>
    <div style="margin: 20px">
        <el-row :gutter="20">
            <el-col :span="11">
                入库时间：
                <el-date-picker
                    value-format="yyyy-MM-dd HH:mm:ss"
                    v-model="sTime"
                    type="datetime"
                    placeholder="选择起始时间">
                </el-date-picker>
                &nbsp;-&nbsp;
                <el-date-picker
                    value-format="yyyy-MM-dd HH:mm:ss"
                    v-model="eTime"
                    type="datetime"
                    placeholder="选择结束时间">
                </el-date-picker>
            </el-col>
            <el-col :span="3">
                <el-button type="primary" style="width: 100px" @click="qyery">查询</el-button>
            </el-col>
        </el-row>
        <br>
        <el-table
            v-loading="loading"
            element-loading-text="拼命加载中"
            element-loading-spinner="el-icon-loading"
            element-loading-background="rgba(0, 0, 0, 0.5)"
            max-height="450"
            :data="tableData.slice((currentPage-1)*pagesize,currentPage*pagesize)"
            border
            style="width: 100%">
            <el-table-column
                prop="RowKey"
                label="RowKey"
                width="180">
            </el-table-column>
            <el-table-column
                prop="Familiy:Quilifier"
                label="列簇&键"
                width="180">
            </el-table-column>
            <el-table-column
                prop="Value"
                label="值">
            </el-table-column>
            <el-table-column
                prop="Time"
                label="入库时间"
                width="180">
            </el-table-column>
        </el-table>
        <br>
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 40]"
            :page-size="pagesize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="tableData.length">
        </el-pagination>
    </div>
</template>

<script>
    export default {
        name: "queryHabseData",
        data() {
            return {
                sTime: '',
                eTime: '',
                currentPage: 1, //初始页
                pagesize: 10,    //    每页的数据
                tableData: [],
                loading: false
            }
        },
        methods: {
            qyery() {

                if (this.sTime == '' || this.eTime == '') {
                    this.$notify({
                        title: '提示',
                        message: '请选择时间',
                        type: 'warning'
                    });
                    return;
                }
                let _self = this;
                this.loading=true;

                this.$axios.post('mvc/hbase/query', {
                    sTime: _self.sTime,
                    eTime: _self.eTime
                }).then(res => {
                    this.loading=false;
                    _self.currentPage = 1;
                    _self.tableData = res.data;
                }).catch(e => {
                    this.loading=false;
                    this.$notify.error({
                        title: '提示',
                        message: e
                    });
                });
            },
            // 初始页currentPage、初始每页数据数pagesize和数据data
            handleSizeChange: function (size) {
                this.pagesize = size;
                console.log(this.pagesize)  //每页下拉显示数据
            },
            handleCurrentChange: function (currentPage) {
                this.currentPage = currentPage;
                console.log(this.currentPage)  //点击第几页
            },
        }
    }
</script>

<style scoped>

</style>
