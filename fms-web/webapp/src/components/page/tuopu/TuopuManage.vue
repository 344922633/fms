<template>
    <div>
        <Row>
            <router-link to="/tuopu"> <Button class="tableBtn" >拓扑图</Button></router-link>
            <router-link to="/tuopuManage"> <Button class="tableBtn" >拓扑图管理</Button></router-link>
            <router-link to="/control"> <Button class="tableBtn" >控件管理</Button></router-link>
        </Row>
        <div class="table">
            <div class="container">
                <el-table :data="tableData.slice((currentPage-1)*pageSize,currentPage*pageSize)" border class="table" ref="multipleTable" >
                    <el-table-column prop="name" label="图片名称" width="120">
                    </el-table-column>
                    <el-table-column label="操作" width="180" align="center">
                        <template slot-scope="scope">
                            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                            <el-button type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
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
        </div>
        <!--   &lt;!&ndash; 编辑弹出框 &ndash;&gt;
           <el-dialog title="编辑" :visible.sync="editVisible" width="30%">
               <el-form ref="form" :model="form" label-width="50px">
                   <el-form-item label="日期">
                       <el-date-picker type="date" placeholder="选择日期" v-model="form.date" value-format="yyyy-MM-dd" style="width: 100%;"></el-date-picker>
                   </el-form-item>
                   <el-form-item label="姓名">
                       <el-input v-model="form.name"></el-input>
                   </el-form-item>
                   <el-form-item label="地址">
                       <el-input v-model="form.address"></el-input>
                   </el-form-item>

               </el-form>
               <span slot="footer" class="dialog-footer">
                   <el-button @click="editVisible = false">取 消</el-button>
                   <el-button type="primary" @click="saveEdit">确 定</el-button>
               </span>
           </el-dialog>-->

        <!-- 删除提示框 -->
        <el-dialog title="提示" :visible.sync="delVisible" width="300px" center>
            <div class="del-dialog-cnt">删除不可恢复，是否确定删除？</div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="delVisible = false">取 消</el-button>
                <el-button type="primary" @click="deleteRow">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                pageSize:20,
                currentPage:1,
                tableData: [],
                // cur_page: 1,
                select_cate: '',
                select_word: '',
                delVisible: false,
                form: {
                    name: '',
                },
                idx: -1
            }
        },
        created() {
            this.getData();
        },
        methods: {
            // 分页导航
     /*       handleCurrentChange(val) {
                this.cur_page = val;
                this.getData();
            },*/
            async getData() {
                let {data} = await this.$axios.post('mvc/picture/getPictureList');
                this.tableData = data;
            },

           handleEdit(index, row) {
               localStorage.setItem('__needRefresh__', '1')
               localStorage.getItem('__needRefresh__')
               this.$router.push(`tuopu?id=${row.id}`);
           },


            handleDelete(index, row) {
                this.idx = index;
                this.delVisible = true;
            },

            // 确定删除
            async deleteRow(){
                await this.$axios.post('mvc/picture/delete', {id: this.tableData[this.idx].id});
                await this.getData();
                this.$message.success('删除成功');
                this.delVisible = false;
            },

            handleCurrentChange(val){
                this.currentPage = val;
            }
        }
    }
</script>

<style scoped>
    .handle-box {
        margin-bottom: 20px;
    }

    .handle-select {
        width: 120px;
    }

    .handle-input {
        width: 300px;
        display: inline-block;
    }
    .del-dialog-cnt{
        font-size: 16px;
        text-align: center
    }
    .table{
        width: 100%;
        font-size: 14px;
    }
    .red{
        color: #ff0000;
    }
</style>
