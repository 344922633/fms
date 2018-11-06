<template>
    <div>
        <Layout>
            <Sider width="250" style="overflow: scroll">
                <Card title="图片分类">
                    <!--<p slot="title" >图片分类</p>-->
                    <p slot="extra">
                        <el-dropdown @command="handleCategoryCommand">
                            <i class="el-icon-setting" style="margin-left:20px;margin-right: 15px"></i>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item command="all">同步所有分类</el-dropdown-item>
                                <el-dropdown-item command="spec">同步该节点下类目</el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                    </p>
                    <div style="overflow: scroll;height: 69vh">
                        <el-tree :data="data"  :props="defaultProps" @node-click="handleNodeClick" :expand-on-click-node="false" v-loading="loading"
                                 element-loading-text="拼命加载中"
                                 element-loading-spinner="el-icon-loading"
                                 element-loading-background="rgba(0, 0, 0, 0.8)"
                                 :render-content="renderContent"
                                 :highlight-current="true"
                                 node-key="id"
                                 @node-contextmenu="handleContextMenu"
                                 :default-expanded-keys="[0]"></el-tree>
                        <vue-context-menu :contextMenuData="contextMenuData"
                                          @addNode="addNode"
                                          @editNode="editNode"
                                          @addPermission="addPermission">
                        </vue-context-menu>
                    </div>


                </Card>

            </Sider>
            <Layout>
                <Content>
                    <Card>
                        <p slot="title">图片管理</p>
                        <p slot="extra">分类：{{ dir }}</p>
                        <el-row class="tool-bar">
                            <el-button round @click="showUpload">上传图片</el-button>
                            <el-button type="primary" @click="syncPictures" round>同步图片</el-button>
                            <el-button type="success" round @click="downloadPictures">下载图片</el-button>
                            <el-button type="info" round @click="deletePictures">删除图片</el-button>
                            <div class="handle-box">
                                <el-input v-model="select_word" placeholder="标题" class="handle-input"></el-input>
                                <el-button type="primary" icon="search" @click="search">搜索</el-button>
                            </div>
                            <!--<el-button type="warning" round @click="handleTest">警告按钮</el-button>-->
                            <!--<el-button type="danger" round>危险按钮</el-button>-->
                        </el-row>
                        <Divider></Divider>
                        <el-table :data="tableData" v-loading="loading"
                                  element-loading-text="拼命加载中"
                                  element-loading-spinner="el-icon-loading"
                                  element-loading-background="rgba(0, 0, 0, 0.8)"
                                  @selection-change="handleSelectionChange"
                                  max-height="390">
                            <el-table-column type="selection" width="55"></el-table-column>
                            <el-table-column prop="pic" label="缩略图" width="140">
                                <template slot-scope="scope">
                                    <img  :src="scope.row.picture_path" alt="" style="width: 70px;height: 70px">
                                </template>
                            </el-table-column>
                            <el-table-column prop="title" label="标题" width="120">
                            </el-table-column>
                            <el-table-column prop="picture_path" label="地址">
                            </el-table-column>
                            <el-table-column prop="sizes" label="大小">
                            </el-table-column>
                            <el-table-column prop="pixel" label="尺寸">
                            </el-table-column>
                            <el-table-column
                                fixed="right"
                                label="操作"
                                width="100">
                                <template slot-scope="scope">
                                    <el-button class="cp-url" :data-path="scope.row.picture_path" type="text" size="small">复制链接</el-button><br/>
                                    <el-button class="cp-code" :data-path="scope.row.picture_path" type="text" size="small">复制代码</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <div class="pagination">
                            <el-pagination @current-change="handleCurrentChange" @size-change="handleSizeChange" :page-sizes="sizes" layout="total, sizes, prev, pager, next" :page-size="pageSize" :total="totalCount">
                            </el-pagination>
                        </div>
                    </Card>

                </Content>
            </Layout>
            <!--<el-container>
                <el-header style="text-align: left; font-size: 12px">
                    <span>图片信息</span>
                    <span></span>
                </el-header>

                <el-main>


                </el-main>
            </el-container>-->
        </Layout>
        <!-- 编辑弹出框 -->
        <el-dialog :title="editTitle" :visible.sync="editVisible" width="30%">
            <el-form ref="form" :model="form" label-width="100px">
                <!--<el-form-item label="日期">
                    <el-date-picker type="date" placeholder="选择日期" v-model="form.date" value-format="yyyy-MM-dd" style="width: 100%;"></el-date-picker>
                </el-form-item>-->
                <el-form-item label="文件夹名称">
                    <el-input v-model="form.name"></el-input>
                </el-form-item>
                <!--<el-form-item label="地址">
                    <el-input v-model="form.address"></el-input>
                </el-form-item>-->

            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">确 定</el-button>
            </span>
        </el-dialog>

        <!-- 删除提示框 -->
        <el-dialog title="提示" :visible.sync="delVisible" width="300px" center>
            <div class="del-dialog-cnt">删除不可恢复，是否确定删除？</div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="delVisible = false">取 消</el-button>
                <el-button type="primary" @click="doDelete">确 定</el-button>
            </span>
        </el-dialog>

        <!-- 上传窗口 -->
        <el-dialog title="提示" :visible.sync="uploadVisible" width="410px" center>
            <el-upload
                class="upload-demo"
                ref="upload"
                drag
                :data="uploadParam"
                action="/mvc/uploadPicture"
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-success="handleUploadSuccess"
                list-type="picture"
                :limit="1"
                multiple>
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过500kb</div>
            </el-upload>
            <span slot="footer" class="dialog-footer">
                <el-button @click="uploadVisible = false">取 消</el-button>
                <el-button type="primary" @click="doUpload">确 定</el-button>
            </span>
        </el-dialog>
        <!--//部门树-->
        <dep-tree title="组织架构" :show.sync="depVisible" @confirm="handleConfirm"></dep-tree>
        <!--<z-dialog :show.sync="depVisible"></z-dialog>-->
    </div>
</template>


<style scoped="scoped">
    .handle-input {
        width: 300px;
    }
    .handle-box {
        /*margin-bottom: 20px;*/
        float: right;
    }
</style>

<script>
    import VueContextMenu from '@/components/common/VueContextMenu.vue'
//    import Clipboard from 'clipboard';
//    import DepTree from '@/components/common/DepTree.vue';
//    import ZDialog from '@/components/common/Test.vue';
    export default {
        components: {
            VueContextMenu,
//            Clipboard,
//            DepTree,
//            ZDialog
        },
        data() {
            const item = {
                date: '2016-05-02',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            };
            return {
                //表格数据
                tableData: [],
                //当前页数
                curPage: 1,
                //总条数
                totalCount: 0,
                //每页条数
                pageSize: 10,
                //分页选项
                sizes: [10, 20, 40, 100],
                loading: false,
                //标题分类展示
                dir: '',
                //选中id
                selection: [],
                //右键选中node
                contextNode: null,
                //单击选中node
                currentNode: null,
                //上传携带参数
                uploadParam: {

                },
                //树节点操作标题
                editTitle: '新增',
                //树右键菜单配置项
                contextMenuData: {
                    // the contextmenu name(@1.4.1 updated)
                    menuName: 'demo',
                    // The coordinates of the display(菜单显示的位置)
                    axios: {
                        x: null,
                        y: null
                    },
                    // Menu options (菜单选项)
                    menulists: [
                        {
                            fnHandler: 'addNode', // Binding events(绑定事件)
                            icoName: 'fa fa-plus-circle fa-fw', // icon (icon图标 )
                            btnName: '新增' // The name of the menu option (菜单名称)
                        },
                        {
                            fnHandler: 'editNode',
                            icoName: 'fa fa-edit fa-fw',
                            btnName: '重命名'
                        },
                        {
                            fnHandler: 'addPermission',
                            icoName: 'fa fa-edit fa-fw',
                            btnName: '添加权限'
                        }
                    ]
                },
                //树右键编辑dialog
                editVisible: false,
                //图片删除确认dialog
                delVisible: false,
                //上传dialog
                uploadVisible: false,
                depVisible: false,
                //节点修改数据模型
                form: {
                    name: '',
                    date: '',
                    address: ''
                },
                //分类数组
                data: [{
                    label: '一级 1',
                    children: [{
                        label: '二级 1-1',
                        children: [{
                            label: '三级 1-1-1'
                        }]
                    }]
                }],
                //标题查询
                select_word: '',
                defaultProps: {
                    children: 'children',
                    label: 'label'
                }
            }
        },
        created() {
//            this.getCategoryData();
        },
        mounted() {
            const copyUrl = new Clipboard('.cp-url', {
                text: function (tr) {
                    return tr.getAttribute('data-path');
                }
            });
            const copyCode = new Clipboard('.cp-code', {
                text: function (tr) {
                    return "<img src='" + tr.getAttribute('data-path') + "'/>";
                }
            });
        },
        methods: {
            //分类管理
            handleNodeClick(data) {
                this.currentNode = data;
                this.dir = data.picture_category_name;
                this.curPage = 1;
                this.uploadParam = {
                    categoryId: data.id
                }
                this.getPictures()
            },
            handleCategoryCommand(command) {
                var parentId = '';
                if (command == 'spec') {
                    parentId = this.currentNode.id;
                }
                this.$axios.post('/mvc/syncPictureCategoryFromTaobao', {
                    parentId: parentId
                }).then(res => {
                    var title = '错误';
                    var type = 'error';

                    if (res.data.success) {
                        title = '成功';
                        type = 'success';
                        this.getCategoryData();
                    }
                    this.$notify({
                        title: title,
                        message: res.data.data,
                        type: type
                    });
                });
            },
            getCategoryData() {
                var me = this;
                me.$axios.post('/mvc/listPictureCategory', {
                    isTree: true
                }).then(res => {
                    this.data = [{
                        id: 0,
                        picture_category_name: '全部图片',
                        children: res.data
                    }];
                });
            },
            renderContent(h, { node, data, store }) {
                var text = node.label;
                if (text && text.length > 13) {
                    text = text.substring(0, 13) + '...';
                }

                return (<el-tooltip placement="left-start">
                    <div slot="content">{node.label}</div>
                <span style="font-size: 12px">{text}</span>
                    </el-tooltip>);
            },
            handleContextMenu(e, data, node, o) {
                e.preventDefault();
                e.stopPropagation();
                this.contextNode = data;
                var x = e.clientX + 2
                var y = e.clientY + 2
                // Get the current location
                this.contextMenuData.axios = {
                    x, y
                }
            },
            addNode() {
                this.editTitle = '添加';
                this.form = {
                    name: ''
                };
                this.editVisible = true;
            },
            editNode() {
                this.editTitle = '编辑';
                this.editVisible = true;
                this.form.name = this.contextNode.picture_category_name;
            },
            addPermission() {
                this.$axios.post('/mvc/openAddPermission').then(res => {
                    if (res.data.success) {
                        this.depVisible = true;
                    }
                });
            },
            saveEdit() {
                var me = this;
                var url = '/mvc/addPictureCategory';
                var data = {
                    parentId: me.contextNode.id,
                    categoryName: me.form.name
                };
                if (me.editTitle == '编辑') {
                    url = '/mvc/updatePictureCategory';
                    data = {
                        categoryName: me.form.name,
                        categoryId : me.contextNode.id,
                        parentId : me.contextNode.parent_id
                    };
                }
                me.$axios.post(url,data).then(res => {
                    var title = '错误';
                    var type = 'error';
                    var msg = '';
                    if (res.data.success) {
                        title = '成功';
                        type = 'success';
                        this.editVisible = false;
                        if (me.editTitle == '编辑') {
                            msg = res.data.data
                            me.contextNode.picture_category_name = me.form.name;
                        } else {
                            msg = '添加成功！';
                            if (!me.contextNode.children) {
                                me.$set(me.contextNode, 'children', []);
                            }
                            me.contextNode.children.push(res.data.data);
                        }
                    } else {
                        msg = res.data.data
                    }
                    this.$notify({
                        title: title,
                        message: msg,
                        type: type
                    });
                });
            },

            //图片管理
            getPictures() {
                var me = this;
                me.$axios.post('/mvc/listPicture', {
                    page: this.curPage,
                    limit: this.pageSize,
                    pictureCategoryId: this.currentNode.id,
                    title: this.select_word
                }).then(res => {
                    if (res.data.success) {
                        this.$message.warning(res.data.data);
                        return ;
                    }
                    this.tableData = res.data.list;
                    this.totalCount = res.data.count;
                });
            },
            showUpload() {
                if (!this.uploadParam || !this.uploadParam.categoryId) {
                    this.$message.error("请选择图片类目！");
                    return ;
                }
                this.uploadVisible = true;
            },
            doUpload() {
                this.$refs.upload.submit();
            },
            handleFileChange(file, list) {

            },
            syncPictures() {
                if (!this.currentNode || !this.currentNode.id) {
                    this.$message.error("请选择图片类目！");
                    return ;
                }
                this.$axios.post('/mvc/syncPictures', {
                    categoryId: this.currentNode.id
                }).then(res => {
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                    this.curPage = 1;
                    this.getPictures();
                });
            },
            handleCurrentChange(val) {
                this.curPage = val;
                this.getPictures();
            },
            handleSizeChange(val) {
                this.pageSize = val;
                this.getPictures();
            },
            handleSelectionChange(val) {
                var me = this;
                me.selection = val;
            },
            deletePictures() {
                var me = this;
                if (me.selection.length > 0) {
                    this.delVisible = true;
                } else {
                    me.$notify({
                        title: '提示',
                        message: '请选择数据！',
                        type: 'warn'
                    })
                }
            },
            doDelete() {
                var me = this;
                var ids = [];
                me.selection.forEach(function (item) {
                    ids.push(item.id)
                })
                me.$axios.post('/mvc/deletePictures', {
                    ids: ids.join(',')
                }).then(res => {
                    this.delVisible = false;
                    if (res.data.success) {
                        this.getPictures();
                    }
                    me.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    })
                });
            },
            handleUploadSuccess(response, file, fileList) {
                this.$refs.upload.clearFiles();
                this.uploadVisible = false;
                this.getPictures();
                this.$notify({
                    title: '提示',
                    message: response.data,
                    type: response.success ? 'success' : 'error'
                })
            },
            search() {
                this.getPictures();
            },
            downloadPictures() {
                var infos = [];
                var me = this;
                me.selection.forEach(function (item) {
                    infos.push({
                        title: item.title,
                        url: item.picture_path
                    })
                });
                me.$axios.post('mvc/downloadPictures', {
                    infoStr: JSON.stringify(infos)
                }, {
                    responseType: 'blob'
                }).then(res => {
                    this.download(res)
                });
            },
            // 下载文件
            download (data) {
                if (!data) {
                    return
                }
                let url = window.URL.createObjectURL(new Blob([data.data]))
                let link = document.createElement('a')
                link.style.display = 'none'
                link.href = url
                let fileName = ''
                if (data.headers['content-disposition']) {
                    let str = data.headers['content-disposition'];
                    fileName = str.substring(str.indexOf('=') + 1)
                }
                link.setAttribute('download', fileName)
                document.body.appendChild(link)
                link.click()
                document.body.removeChild(link)
            },
            handleConfirm(node) {
                console.log(node)
                console.log(this.contextNode)
                this.$axios.post('/mvc/addPictureCategoryPermission', {
                    depId: node.id,
                    pictureCategoryId: this.contextNode.id
                }).then(res => {
                    if (res.data.success) {
                        this.depVisible = false;
                    }
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' :'warn'
                    })
                })
            },
            handleTest() {
                this.$router.push('/HPRMS_BASE_BRAND');
            }
        }
    };
</script>

