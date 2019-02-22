<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="12">
                <el-card shadow="hover">
                    <schart ref="bar" class="schart" canvasId="bar" :data="data" type="bar" :options="options"></schart>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card shadow="hover">
                    <schart ref="line" class="schart" canvasId="line" :data="data1" type="line" :options="options2"></schart>
                </el-card>
            </el-col>
        </el-row>


        <!--文件解析详情-->
        <el-date-picker
            value-format="yyyy-MM-dd"
            v-model="value1"
            type="date"
            placeholder="选择日期">
        </el-date-picker>
        <el-date-picker
            value-format="yyyy-MM-dd"
            v-model="value2"
            align="right"
            type="date"
            placeholder="选择日期"
        >
        </el-date-picker>
        <el-button size="mini" type="primary" @click="getTimeCount">查询</el-button>
        <el-table
            :data="analyseFiles"
            border
            style="width: 49%">
            <el-table-column
                prop="parsedFileAmount"
                label="已解析文件数"
                width="180">
            </el-table-column>
            <el-table-column
                prop="UnresolvedFileAmount"
                label="未解析文件数"
                width="180">
            </el-table-column>
            <el-table-column
                prop="allFileAmount"
                label="文件总数">
            </el-table-column>
            <el-table-column
                prop="Percent"
                label="已解析 / 总文件">
            </el-table-column>
        </el-table>
        <br>
<!--上传文件详情-->
        <div>
        <el-date-picker
            value-format="yyyy-MM-dd"
            v-model="value3"
            type="date"
            placeholder="选择日期">
        </el-date-picker>
        <el-date-picker
            value-format="yyyy-MM-dd"
            v-model="value4"
            align="right"
            type="date"
            placeholder="选择日期"
            >
        </el-date-picker>
        <el-button size="mini" type="primary" @click="getUploadTimeCount">查询</el-button>
            <span>&nbsp;&nbsp;期间文件总数: {{ fileSum }}</span>
        </div>
        <el-table
            :data="fileCount"
            border
            style="width: 49%">
            <el-table-column
                prop="realPath"
                label="文件路径"
                width="180">
            </el-table-column>
            <el-table-column
                prop="name"
                label="文件名"
                width="180">
            </el-table-column>
            <el-table-column
                prop="type"
                label="文件类型">
            </el-table-column>
            <el-table-column
                prop="addTime"
                label="上传时间">
            </el-table-column>
        </el-table>

    </div>
</template>

<script>
    import Schart from 'vue-schart';
    import bus from '../../common/bus';
    export default {
        name: 'dashboard',
        data() {
            return {
                name: localStorage.getItem('ms_username'),
                todoList: [{
                    title: '今天要修复100个bug',
                    status: false,
                },
                    {
                        title: '今天要修复100个bug',
                        status: false,
                    },
                    {
                        title: '今天要写100行代码加几个bug吧',
                        status: false,
                    }, {
                        title: '今天要修复100个bug',
                        status: false,
                    },
                    {
                        title: '今天要修复100个bug',
                        status: true,
                    },
                    {
                        title: '今天要写100行代码加几个bug吧',
                        status: true,
                    }
                ],
                data1: [],
                data: [{
                    name: '2018/09/04',
                    value: 1083
                },
                    {
                        name: '2018/09/05',
                        value: 941
                    },
                    {
                        name: '2018/09/06',
                        value: 1139
                    },
                    {
                        name: '2018/09/07',
                        value: 816
                    },
                    {
                        name: '2018/09/08',
                        value: 327
                    },
                    {
                        name: '2018/09/09',
                        value: 228
                    },
                    {
                        name: '2018/09/10',
                        value: 1065
                    }
                ],
                options: {
                    title: '文件分类统计',
                    showValue: false,
                    fillColor: 'rgb(45, 140, 240)',
                    bottomPadding: 30,
                    topPadding: 30
                },
                options2: {
                    title: '文件后缀统计',
                    fillColor: '#FC6FA1',
                    axisColor: '#008ACD',
                    contentColor: '#EEEEEE',
                    bgColor: '#F5F8FD',
                    bottomPadding: 30,
                    topPadding: 30
                },
                value1: '',
                value2: '',
                value3: '',
                value4: '',
                analyseFiles:null,
                fileCount:null,
                fileSum:null
            }
        },
        components: {
            Schart
        },
        computed: {
            role() {
                return this.name === 'admin' ? '超级管理员' : '普通用户';
            }
        },
        created(){
            this.handleListener();
            this.getData()
            this.changeDate();
            this.getAnalyseFiles();
        },
        activated(){
            this.handleListener();
        },
        deactivated(){
            window.removeEventListener('resize', this.renderChart);
            bus.$off('collapse', this.handleBus);
        },
        methods: {
            getAnalyseFiles(){
                //获取文件解析数据
                this.$axios.get('mvc/getAnalyseFiles').then(res => {
                    this.analyseFiles = res.data;
            })
            },
            getTimeCount(){

                //获取文件解析数据
                let obj = {};
                if(this.value1==''||this.value2==''){
                    alert("请填写完整日期后查询")
                    return;
                }
                obj["startTime"] = this.value1;
                obj["endTime"] = this.value2;

                this.$axios.post('mvc/getTimeCount', {
                    param:JSON.stringify(obj),
                }).then(res => {
                    this.analyseFiles = res.data;
                });


            },
            //文件详情
            getUploadTimeCount(){

                let obj = {};
                if(this.value3==''||this.value4==''){
                    alert("请填写完整日期后查询")
                    return;
                }
                obj["startTime"] = this.value3;
                obj["endTime"] = this.value4;


                this.$axios.post('mvc/getFileSum', {
                    param:JSON.stringify(obj),
                }).then(res => {
                    this.fileSum = res.data;
                });


                this.$axios.post('mvc/getUploadTimeCount', {
                    param:JSON.stringify(obj),
                }).then(res => {
                    this.fileCount = res.data;
                });


            },


            changeDate(){
                const now = new Date().getTime();
                this.data.forEach((item, index) => {
                    const date = new Date(now - (6 - index) * 86400000);
                item.name = `${date.getFullYear()}/${date.getMonth()+1}/${date.getDate()}`
            })
            },
            handleListener(){
                bus.$on('collapse', this.handleBus);
                // 调用renderChart方法对图表进行重新渲染
                window.addEventListener('resize', this.renderChart)
            },
            handleBus(msg){
                setTimeout(() => {
                    this.renderChart()
            }, 300);
            },
            renderChart(){
                this.$refs.bar.renderChart();
                this.$refs.line.renderChart();
            },
            getData() {
                this.$axios.post('mvc/getFileStatistic').then(res => {
                    this.data = res.data;
            })
                this.$axios.post('mvc/getFileSuffixStatistic').then(res => {
                    this.data1 = res.data;
            })
            },


        }
    }

</script>


<style scoped>
    .el-row {
        margin-bottom: 20px;
    }

    .grid-content {
        display: flex;
        align-items: center;
        height: 100px;
    }

    .grid-cont-right {
        flex: 1;
        text-align: center;
        font-size: 14px;
        color: #999;
    }

    .grid-num {
        font-size: 30px;
        font-weight: bold;
    }

    .grid-con-icon {
        font-size: 50px;
        width: 100px;
        height: 100px;
        text-align: center;
        line-height: 100px;
        color: #fff;
    }

    .grid-con-1 .grid-con-icon {
        background: rgb(45, 140, 240);
    }

    .grid-con-1 .grid-num {
        color: rgb(45, 140, 240);
    }

    .grid-con-2 .grid-con-icon {
        background: rgb(100, 213, 114);
    }

    .grid-con-2 .grid-num {
        color: rgb(45, 140, 240);
    }

    .grid-con-3 .grid-con-icon {
        background: rgb(242, 94, 67);
    }

    .grid-con-3 .grid-num {
        color: rgb(242, 94, 67);
    }

    .user-info {
        display: flex;
        align-items: center;
        padding-bottom: 20px;
        border-bottom: 2px solid #ccc;
        margin-bottom: 20px;
    }

    .user-avator {
        width: 120px;
        height: 120px;
        border-radius: 50%;
    }

    .user-info-cont {
        padding-left: 50px;
        flex: 1;
        font-size: 14px;
        color: #999;
    }

    .user-info-cont div:first-child {
        font-size: 30px;
        color: #222;
    }

    .user-info-list {
        font-size: 14px;
        color: #999;
        line-height: 25px;
    }

    .user-info-list span {
        margin-left: 70px;
    }

    .mgb20 {
        margin-bottom: 20px;
    }

    .todo-item {
        font-size: 14px;
    }

    .todo-item-del {
        text-decoration: line-through;
        color: #999;
    }

    .schart {
        width: 100%;
        height: 300px;
    }

</style>
