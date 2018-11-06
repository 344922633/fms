<template>
    <div>
        <div>
            <Upload
                :on-success="handleSuccess"
                :show-upload-list="false"
                action="mvc/blockManage/import">
                <Button icon="ios-cloud-upload-outline">导入</Button>
            </Upload>
        </div>
        <Row>
            <i-col span="12" style="padding-right: 5px">
                <div class="content-title">黑名单</div>
                <div>
                        <textarea  name="black_block"  v-model="blockInfo.blackContent" style="width:100%;min-height:300px;overflow:scroll;resize:none;" >
                            {{blockInfo.blackContent}}
                        </textarea>
                </div>
                <!--<div style="text-align: right">
                    <el-button type="primary" @click="addOrUpdate('black')">更新</el-button>
                </div>-->
            </i-col>
            <i-col span="12" style="padding-left: 5px">
                <div class="content-title">白名单</div>

                <div>
                        <textarea  name="white_block"  v-model="blockInfo.whiteContent" style="width:100%;min-height:300px;overflow:scroll;resize:none;" >
                            {{blockInfo.whiteContent}}
                        </textarea>
                </div>
                <!--<div style="text-align: right">
                    <el-button type="primary" @click="addOrUpdate('white')">更新</el-button>
                </div>-->
            </i-col>
        </Row>
        <div><Button type="primary" long @click="handleSave">确定</Button></div>
    </div>
</template>

<script>
    import ICol from "../../../node_modules/iview/src/components/grid/col.vue";

    export default {
        components: {ICol},
        name: 'basetable',
        data() {
            return {
                url: 'mvc/blockManage/getList',
                updateUrl: 'mvc/blockManage/updateById',
                addUrl: 'mvc/blockManage/add',
                black_content:this.blockInfo.blackContent,
                black_id: '',
                white_content:this.blockInfo.whiteContent,
                white_id: ''
            }
        },
        props: {
            parser: {
                type: Object,
                default: {}
            },
            blockInfo: {
                type: Object,
                default: {}
            }
        },
        created() {
//            this.getData();
        },
        methods: {

            // 获取数据
            getData() {
                this.$axios.post(this.url, {
                    parserId: this.parserId
                }).then((res) => {
                    var data =  res.data;
                    if(data != null) {
                        for(var i=0;i<data.length;i++) {
                            if (data[i].blockType == 0) {
                                this.black_content =  data[i].content;
                                this.black_id = data[i].id;
                            } else {
                                this.white_content =  data[i].content;
                                this.white_id = data[i].id;
                            }
                        }
                    }
                })
            },

            // 保存编辑
            addOrUpdate(type) {
                var id;
                var content;
                //0:黑名单；1：白名单
                var blockType = 0;
                var addOrUpdateUrl = this.updateUrl;
                //如果是黑名单，则设置更新id为黑名单id，内容为黑名单内容，反之则是白名单
                if (type == 'black') {
                    id = this.black_id;
                    content = this.black_content;
                } else {
                    id = this.white_id;
                    content = this.white_content;
                    blockType = 1;
                }

                //如果id为空，说明之前没添加过，则设置请求路径为添加路径
                if(id == null || id.length == 0) {
                    addOrUpdateUrl = this.addUrl;
                }
                this.$axios.post(addOrUpdateUrl, {
                    id: id,
                    content:content,
                    blockType:blockType
                }).then((res) => {
                    if(res.data.success) {
                        this.$message.success(`操作成功`);
                        this.getData();
                    } else {
                        this.$message.success(res.data.data);
                    }
                })
            },
            //上传成功后的回调
            handleSuccess (res, file) {
                let black = this.blockInfo.blackContent;
                let white = this.blockInfo.whiteContent;
                this.blockInfo.blackContent = (black ? black + ',' : black) + res.black;
                this.blockInfo.whiteContent = (white ? white + ',' : white) + res.white;
            },
            handleSave() {
                let url = this.addUrl;
                if (this.blockInfo.id) {
                    url = this.updateUrl;
                }
                //this.blockInfo.fileParserId = this.parser.id;
                this.$axios.post(url, this.blockInfo).then(res => {
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                    if (res.data.success) {
                        this.$emit('after-close');
                    }
                });
            }
        }
    }
</script>

<style scoped>

    .content-title{
        clear: both;
        font-weight: 400;
        line-height: 50px;
        margin: 10px 0;
        font-size: 22px;
        color: #1f2f3d;
    }

</style>
