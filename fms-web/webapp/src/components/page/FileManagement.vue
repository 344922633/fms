<style scoped>
    .tablePage {
        margin-top: 2px;
    }
    .tableClass{
        margin-top: 2px;
    }
    tr{
        border-style: none;
    }
    td{
        border-style: none;
    }
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
                        <el-tree :data="treeData"  :props="defaultProps" @node-click="handleNodeClick" :expand-on-click-node="false"
                                 element-loading-text="拼命加载中"
                                 element-loading-spinner="el-icon-loading"
                                 element-loading-background="rgba(0, 0, 0, 0.8)"
                                 :render-content="renderContent"
                                 :highlight-current="true"
                                 node-key="id"
                                 @node-contextmenu="handleContextMenu"
                                 :default-expanded-keys="[0]"></el-tree>
                        <!--目录右键菜单-->
                        <vue-context-menu :contextMenuData="contextMenuData"
                                          @addNode="addNode"
                                          @editNode="editNode"
                                          @deleteNode="deleteNode">
                        </vue-context-menu>
                    </div>


                </Card>

            </Sider>
            <Layout>
                <div class="table">
                    <div class="container" style="padding: 0; border: none">
                        <Card title="文件" style="padding: 0">
                            <div class="tableTop">
                                <Button @click="fileUpload">文件上传</Button>
                                <!--上传弹出窗口-->
                                <Modal
                                    title="上传"
                                    v-model="modal8"
                                    @on-ok="refreshData"
                                    @on-cancel="refreshData"
                                    :mask-closable="false">
                                    <!--上传组建-->
                                    <uploader :options="options" :file-status-text="statusText" class="uploader-example" ref="uploader"
                                              @file-complete="fileComplete" @complete="complete" @file-success="fileSuccess">
                                        <uploader-unsupport></uploader-unsupport>
                                        <uploader-drop>
                                            <p>拖拽文件或文件夹到这里 或者</p>
                                            <uploader-btn>选择文件</uploader-btn>
                                            <uploader-btn :directory="true">选择文件夹</uploader-btn>
                                        </uploader-drop>
                                        <uploader-list></uploader-list>
                               请选则一个目录！     </uploader>
                                </Modal>
                                <!--上传弹出框用于确认当前选中目录-->
                                <Modal
                                    title="上传"
                                    v-model="modal1"
                                    :mask-closable="false"
                                    @on-ok="ok"
                                    @on-cancel="cancel">
                                    <p>当前选中目录名称为 >> {{ tDirectoryText }}</p>
                                </Modal>
                                 <!--上传弹出框用于确认是直接上传还是ftp上传-->
                                <Modal
                                    title="上传方式"
                                    v-model="modalUploadChoose"
                                    :mask-closable="false"
                                    >
                                   <Button @click="uploadFile">文件上传</Button>
                                   <Button @click="uploadFromFtp">ftp下载</Button>
                                </Modal>
                                <Modal
                                    title="ftp下载"
                                    v-model="modalUploadFromFtp"
                                    :mask-closable="false"
                                    @on-ok="uploadOK">
                                    <div>
                                         <table border="1" style="border-style: none; ">
                                             <tr>
                                                 <td>ip：</td>
                                                 <td>
                                                     <Input v-model="ftpInfo.ip" style="width:300px;"/>
                                                 </td>
                                             </tr>
                                             <tr>
                                                 <td>用户名：</td>
                                                 <td>
                                                     <Input v-model="ftpInfo.userName" style="width:300px;"/>
                                                 </td>
                                             </tr>
                                             <tr>
                                                 <td>密码：</td>
                                                 <td>
                                                     <Input v-model="ftpInfo.password" style="width:300px;" type="password"/>
                                                 </td>
                                             </tr>
                                             <tr>
                                                 <td>端口号：</td>
                                                 <td>
                                                     <Input v-model="ftpInfo.port" style="width:300px;"/>
                                                 </td>
                                             </tr>
                                             <tr>
                                                 <td>下载路径：</td>
                                                 <td>
                                                     <Input v-model="ftpInfo.path" style="width:300px;"/>
                                                 </td>
                                             </tr>
                                         </table>
                                     </div>
                                </Modal>

                                <Button @click="previewtFile">预览</Button>
                                <!--预览弹出窗口-->
                                <Modal
                                    title="文件预览"
                                    width="840px"
                                    scrollable
                                    v-model="modalPreviewFile"
                                    :mask-closable="false">
                                    <div>
                                        <iframe :src="previewFileData" height="600px" width="800px"></iframe>
                                    </div>
                                </Modal>
                                <Button @click="updatebFile">修改</Button>
                                <!--修改窗口-->
                                <Modal
                                    title="修改"
                                    v-model="modalUpdate"
                                    :mask-closable="false"
                                    @on-ok="updatetFileInfo">
                                    <div>
                                        <table border="1" style="border-style: none; ">
                                            <tr>
                                                <td>文件名：</td>
                                                <td>
                                                    <Input v-model="ftpInfo.ip" style="width:300px;"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>分类：</td>
                                                <td>
                                                    <Select v-model="selectNFile.selectClassId" style="width:300px">
                                                        <Option v-for="item in fileTypeData" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                                    </Select>
                                                </td>
                                            </tr>
                                            <!--<tr>-->
                                                <!--<td>推荐解析器：</td>-->
                                                <!--<td>-->
                                                    <!--<Select v-model="fileRecommendParserName" @on-change="updateSelectFileRecommendParserName" style="width:300px">-->
                                                        <!--<Option v-for="item in fileParserData" :value="item.id" :key="item.id">{{ item.name }}</Option>-->
                                                    <!--</Select>-->
                                                <!--</td>-->
                                            <!--</tr>-->
                                            <tr>
                                                <td>描述信息：</td>
                                                <td>
                                                    <Input v-model="selectNFile.selectFileDescription"  type="textarea" style="width:300px" :rows="4"  />
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </Modal>
                                <Button @click="deletetFile">删除</Button>
                                <!--删除确认窗口-->
                                <Modal
                                    title="提示"
                                    v-model="modalDelete"
                                    :mask-closable="false"
                                    @on-ok="deleteFileInfo">
                                   <p>确认要删除数据吗？</p>
                                </Modal>
                                <Button @click="removebFile">移动</Button>
                                <!--移动目标目录选择框-->
                                <Modal
                                    title="目录"
                                    v-model="modalRemove"
                                    :mask-closable="false"
                                    @on-ok="removetFile">
                                    <Tree :data="treeData" @on-select-change="removeOnSelectChange"></Tree>
                                </Modal>

                            </div>
                            <!--文件列表表格-->
                            <i-table class="tableClass" size="small"  :content="self" height="395" type="selection" :columns="columns4" :data="tableData"  @on-selection-change="selectRowChange"></i-table>
                            <div class="tablePage">
                                <!--分页组建-->
                                <Page :total="total" :page-size="pageSize" :page-size-opts="pageSizeOpts" :current="current" @on-change="handleCurrentChange" @on-page-size-change="handleSizeChange" show-sizer />
                            </div>
                        </Card>

                    </div>

                </div>

            </Layout>
        </Layout>
            <!-- 目录右键编辑弹出框 -->
            <el-dialog :title="editTitle" :visible.sync="editVisible" width="30%">
                <el-form ref="form" :model="form" label-width="100px">
                    <el-form-item label="文件夹名称">
                        <el-input v-model="form.name"></el-input>
                    </el-form-item>

                </el-form>
                <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">确 定</el-button>
            </span>
            </el-dialog>

            <!-- 目录右键删除提示框 -->
            <el-dialog title="提示" :visible.sync="delVisible" width="300px" center>
                <div class="del-dialog-cnt">删除不可恢复，是否确定删除？</div>
                <span slot="footer" class="dialog-footer">
                <el-button @click="delVisible = false">取 消</el-button>
                <el-button type="primary" @click="doDeleteTreeNode">确 定</el-button>
            </span>
            </el-dialog>

        <!--解析页面-->

        <Modal v-model="parserVisible" fullscreen footer-hide @on-visible-change="changeParserVisible">
            <single-parser  :file="currentFile" :parserData="parserData" :parserExtList="parserExtList" :columnData="tableColumnData" @after-close="parserVisible = false"></single-parser>
        </Modal>
        <!--遮罩-->
        <Spin size="large" fix v-if="loading">
            <Icon type="ios-loading" size=18 class="demo-spin-icon-load"></Icon>
            操作中。。。
        </Spin>
    </div>
</template>


<script>
    // import LyTree from '@/components/common/vue-tree/tree.vue';
    import VueContextMenu from '@/components/common/VueContextMenu.vue';
    import SingleParser from '@/components/page/fileParser/SingleFileParser.vue'
    import Bus from '@/components/common/bus'
    import qs from 'qs';
    import fileMd5 from 'browser-md5-file';
   

    export default {
        name: "FileManagement",
        components: {
            VueContextMenu,
            SingleParser
        },
        data() {
            return {
                parserExtList:[],
                config: {},
                modal8: false,//上传弹出窗口
                modal1: false,//上传弹出框用于确认当前选中目录
                modalUploadChoose: false,
                modalUploadFromFtp: false,
                modalUpdate:false,//修改窗口
                modalRemove:false,//移动目标目录选择框
                modalPreviewFile:false,//预览弹出窗口
                modalDelete:false,//删除确认窗口
                parserVisible: false,//解析页面
                parserData: [],//解析窗口解析器列表
                selectNFile:{//当前选中行数据
                    selectFileName:'',
                    selectFileId:'',
                    selectFileDescription:'',
                    selectFileType:'',
                    selectClassName:'',
                    selectFileRecommendParserName:'',
                    selectClassId:'',
                    selectFileRecommendParserId:''
                },
                ftpInfo:{//当前选中行数据
                    ip:'',
                    userName:'',
                    password:'',
                    port:'',
                    path:''
                },
                currentFile: null,//解析窗口需要的选中操作文件
                removeFileOfDirectoryId:'',//文件移动的目的地文件夹ID

                treeData: [],//目录树列表
                fileTypeData:[],//文件类型列表
                fileParserData:[],//文件解析器列表
                fileRecommendParserName:'',//修改文件时选中的文件解析器的解析器名称-业务需求已废弃
                url:'mvc/pageFiles',//文件分页列表地址
                self:this,
                // parserInfo: {
                //     path: '',
                //     parserId: null
                // },
                columns4: [//文件列表表头
                    {
                        type: 'selection',
                        width: 50,
                        align: 'center'
                    },
                    {
                        title: '文件名',
                        key: 'name'
                    },
                    {
                        title: '描述信息',
                        key: 'description'
                    },
                    {
                        title: '分类名称',
                        key: 'className'
                    },
                    {
                        title: '推荐解析器',
                        key: 'recommendParserName'
                    },
                    {
                        title: '是否解析',
                        key: 'isParser',
                        render: (h, params) => {
                            return h('span',{}, params.row.isParser == 1 ? '是' : '否');
                        }
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 150,
                        align: 'center',
                        fixed: 'right',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            this.downloadFile(params.row.id)
                                        }
                                    }
                                }, '下载'),
                                ' | ',
                                h('a', {
                                    on: {
                                        click: () => {
                                            this.handleParse(params.row);
                                        }
                                    }
                                }, '解析')
                            ]);
                        }
                    }
                ],
                // modeInfoMessage:'',
                tableData: [],//文件列表
                total:0,//默认总条数
                pageSize:10,//默认每页显示条数
                current:1,//默认第几页
                pageSizeOpts:[10,20,40,100],//分页组建可选每页显示条数
                selectFileList:[],//当前选中文件
                loading: false,//遮罩
                //右键选中node
                contextNode: null,
                //单击选中node
                currentNode: null,
                //树节点操作标题
                editTitle: '新增',
                //树右键菜单配置项
                contextMenuData: {
                    menuName: 'demo',
                    axios: {
                        x: null,
                        y: null
                    },
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
                            fnHandler: 'deleteNode',
                            icoName: 'fa fa-edit fa-fw',
                            btnName: '删除'
                        }
                    ]
                },
                //树右键编辑dialog
                editVisible: false,
                tDirectoryId:0,//当前选中目录树node节点id
                tDirectoryText:'',//当前选中目录树节点名称
                //分类数组
               // treeData: [],
                //节点修改数据模型
                form: {
                    name: '',
                    date: '',
                    address: ''
                },
                defaultProps: {//目录树默认读取参数
                    children: 'children',
                    label: 'label'
                },
                delVisible: false,//目录删除弹出框
                options: {//文件分片上传数据配置
                    target: process.env.BASE_UPLOAD + 'mvc/chunk',
                    testChunks: true,
                    simultaneousUploads: 1,
                    preprocess: this.preprocess,
                    chunkSize: 1024 * 1024 * 5
                },
                statusText: {
                    success: '成功了',
                    error: '出错了',
                    uploading: '上传中',
                    paused: '暂停中',
                    waiting: '等待中'
                },
                previewFileData:'',
                //解析页面表字段数据
                tableColumnData: []
            }
        },
        created() {
            this.$axios.post('mvc/getConfig').then(res => {
                this.config = res.data;
            });
            this.getTreeDate();
            this.getFileTypeData();
        },
        methods: {
            //刷新当前页面目录树及文件列表
            refreshData(){
                this.getTreeDate();
                this.getData();
            },
            //文件md5
            preprocess(chunk){
               if (chunk.file.md5 === '' || chunk.file.md5 == null) {
                    fileMd5(chunk.file.file, function (err, md5)  {
                        chunk.file.uniqueIdentifier=md5;
                        chunk.preprocessFinished()
                   })
                } else {
                    chunk.preprocessFinished()
                }
            },
            //分页页码
            handleCurrentChange(val) {
                this.current = val;
                this.getData();
            },
            //分页单页条数
            handleSizeChange(val) {
                this.pageSize = val;
                this.getData();
            },
            //获取文件列表
            getData() {
                this.$axios.post(this.url, {
                    directoryId:this.tDirectoryId,
                    page: this.current,
                    limit: this.pageSize
                }).then((res) => {
                    this.tableData = res.data.list;
                    this.total = res.data.count;
                    this.selectFileList=[];
                })
            },
            //递归格式化数据
            convert(childNodes){
                for (var i = 0; i < childNodes.length; i++) {

                    if (childNodes[i].text == '根目录')
                   {
                    childNodes[i].label = localStorage.getItem('ms_username');
                   }
                   else
                   {
                   childNodes[i].label = childNodes[i].text;
                   }
                    childNodes[i].title = childNodes[i].text;
                    if (childNodes[i].children) {
                        this.convert(childNodes[i].children)
                    }
                }
            },
            //获取目录树
            getTreeDate() {
                var treeUrl='mvc/getDirTree';
                this.$axios.post(treeUrl, {
                }).then((res) => {
                    this.treeData = res.data;
                    this.convert(this.treeData)
                })
            },
            //获取文件类型列表
            getFileTypeData(){
                var fileTypeUrl='mvc/fileType/listAll';
                this.$axios.post(fileTypeUrl, {
                }).then((res) => {
                    this.fileTypeData = res.data;
                    // this.convert(this.treeData)
                })
            },
            //获取文件解析器列表
            getFileParserData(){
                var fileParserUrl='mvc/fileParser/getList';
                this.$axios.post(fileParserUrl, {
                }).then((res) => {
                    this.fileParserData = res.data;
                    // this.convert(this.treeData)
                })
            },
            //列表选中时赋值
            selectRowChange(selection){
                this.selectFileList = selection;
            },
            //分类管理
            handleNodeClick(data) {
                this.current=1;
                this.tDirectoryId=data.id;
                this.tDirectoryText=data.text;
                this.getData();
            },
            //目录树名称过长处理
            renderContent(h, { node, data, store }) {
                var text = node.label;
                if (text && text.length > 13) {
                    text = text.substring(0, 13) + '...';
                }

                return (<el-tooltip placement="right-start">
                    <div slot="content">{node.label}</div>
                <span style="font-size: 12px">{text}</span>
                    </el-tooltip>);
            },
            //目录树右键菜单
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
            //目录树右键添加
            addNode() {
                this.editTitle = '添加';
                this.form = {
                    name: ''
                };
                this.editVisible = true;
            },
            //目录树右键编辑
            editNode() {
                this.editTitle = '编辑';
                this.editVisible = true;
                this.form.name = this.contextNode.picture_category_name;
            },
            //目录树右键删除确认
            deleteNode(){
                this.editTitle = '删除';
                this.delVisible = true;
            },
            //目录树右键删除
            doDeleteTreeNode() {
                var me = this;
                this.delVisible = false;
                this.loading=true;
                me.$axios.post('mvc/deleteDir', {
                    id:me.contextNode.id
                }).then(res => {

                    if (res.data.success) {

                    }
                    this.getTreeDate();
                    this.getData();
                    this.loading=false;
                    me.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    })
                });
            },
            // 上传完成
            complete() {
            },
            //上传合并
            fileSuccess(rootFile, file, message, chunk) {
                this.$axios.post('mvc/mergeFile', {
                    filename: file.name,
                    identifier: file.uniqueIdentifier,
                    totalSize: file.size,
                    type: file.type,
                    location: rootFile.path,
                    webkitRelativePath:file.file.webkitRelativePath,
                    directoryId:this.tDirectoryId,
                }).then(function (response) {

                }).catch(function (error) {

                });
            },
            // 一个根文件（文件夹）成功上传完成。
            fileComplete() {

            },
            //文件上传
            fileUpload(){
                if(this.tDirectoryId==0 || this.tDirectoryText==''){
                    this.$Modal.info({
                        title: '提示',
                        content: '请选则一个目录！'
                    });
                }else{
                    this.modal1 = true;
                }

            },
            //上传目录确认后显示上传窗口
            ok () {
                this.modalUploadChoose = true;
            },
            ////上传目录取消后不显示上传窗口
            cancel () {
                this.modalUploadChoose = false;
            },

            uploadFile () {
                this.modal8 = true;
            },

            uploadFromFtp () {
                this.modalUploadFromFtp = true;
            },

            uploadOK() {
                this.$axios.post('mvc/uploadFromFtp', {
                    ipAddr:this.ftpInfo.ip,
                    port:this.ftpInfo.port,
                    userName:this.ftpInfo.userName,
                    pwd:this.ftpInfo.password,
                    path:this.ftpInfo.path,
                    directoryId:this.tDirectoryId

                }).then(res => {
                    if(res.data.success){
                        this.$notify({
                            title: '提示',
                            message: '上传成功！',
                            type: 'success'
                        });
                    refreshData();
                    }else{
                        this.$notify({
                            title: '提示',
                            message: '上传失败！',
                            type: 'error'
                        });
                    }
                }).catch(e => {


                });
            },

            //目录树右键编辑和添加保存
            saveEdit() {
                var me = this;
                var url = '';
                var data = {
                    // parentId: me.contextNode.id,
                    // categoryName: me.form.name
                };
                if (me.editTitle == '添加') {
                    url = 'mvc/addDir';

                    data = {
                        text:me.form.name,
                        parentId:me.contextNode.id
                    };
                }
                if (me.editTitle == '编辑') {
                    url = 'mvc/updateDir';

                    data = {
                        id:me.contextNode.id,
                        text:me.form.name,
                        parentId:me.contextNode.parentId
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
                            // me.contextNode.picture_category_name = me.form.name;
                        } else {
                            msg = '操作成功！';

                          // textNode.children.push(res.data.data);

                        }
                        this.getTreeDate();
                    } else {
                        msg = res.data.data;
                    }
                    this.$notify({
                        title: title,
                        message: msg,
                        type: type
                    });
                });
            },
            //文件单行选中赋值
            setSelectNFile(){
                this.selectNFile.selectFileId=this.selectFileList[0].id,
                this.selectNFile.selectFileName=this.selectFileList[0].name,
                this.selectNFile.selectFileType=this.selectFileList[0].type,
                this.selectNFile.selectFileDescription=this.selectFileList[0].description,
                this.selectNFile.selectClassName=this.selectFileList[0].className,
                this.selectNFile.selectFileRecommendParserName=this.selectFileList[0].recommendParserName,
                this.selectNFile.selectClassId=this.selectFileList[0].classId,
                this.selectNFile.selectFileRecommendParserId=this.selectFileList[0].recommendParserId
            },
            previewtFile() { //预览
                if(this.selectFileList.length==0){
                    this.$Modal.info({
                        title: '提示',
                        content: '请选则一条记录'
                    });
                }else if(this.selectFileList.length>1) {
                    this.$Modal.info({
                        title: '提示',
                        content: '一次只能预览一个文件！'
                    });
                }else{
                    // this.loadHtml();
                    let fileServerPath = this.config.fileServerPath;
                    let previewPath = this.config.previewPath;
                    let fileUrl = fileServerPath + '/' +  this.selectFileList[0].groups + '/'+ this.selectFileList[0].realPath;
                    this.previewFileData = previewPath + encodeURIComponent(fileUrl);
                    this.modalPreviewFile = true
                }
            },
            //修改功能保存选中分类
            // updateSelectFileType(value){
            //     this.selectNFile.selectClassId=value;
            // },
            // updateSelectFileRecommendParserName(value){
            //     this.selectNFile.selectFileRecommendParserId=value;
            // },
            updatebFile() { //修改

                if(this.selectFileList.length==0){
                    this.$Modal.info({
                        title: '提示',
                        content: '请选则一条记录'
                    });
                }else if(this.selectFileList.length>1) {
                    this.$Modal.info({
                        title: '提示',
                        content: '一次只能修改一个文件！'
                    });
                }else{

                    this.setSelectNFile();
                    this.getFileTypeData();
                    this.fileRecommendParserName = this.selectNFile.selectFileRecommendParserId;
                    this.modalUpdate=true
                }
            },
            updatetFileInfo() { //修改
                this.$axios.post('mvc/updateFile', {
                    id: this.selectNFile.selectFileId,
                    name:this.selectNFile.selectFileName,
                    description:this.selectNFile.selectFileDescription,
                    type:this.selectNFile.selectFileType,
                    classId:this.selectNFile.selectClassId,
                    recommendParserId:this.selectNFile.selectFileRecommendParserId
                }).then(res => {
                    if(res.data.success){
                        this.$notify({
                            title: '提示',
                            message: '修改成功！',
                            type: 'success'
                        });
                        this.getData();
                    }else{
                        this.$notify({
                            title: '提示',
                            message: '修改失败！',
                            type: 'error'
                        });
                    }

                })
            },
            deletetFile() { //删除弹窗

                if(this.selectFileList.length==0){
                    this.$Modal.info({
                        title: '提示',
                        content: '至少选择一条数据'
                    });
                }else{
                    this.modalDelete=true;
                }
            },
            //删除操作
            deleteFileInfo(){
                var allSize = this.selectFileList.length;
                var ids ="";
                this.loading=true;
                for(var i=0;i<allSize;i++) {
                    ids=ids+","+this.selectFileList[i].id ;
                }
                this.$axios.post('mvc/deleteFile', {
                    ids: ids
                }).then(res => {
                    if(res.data.success){
                        this.$notify({
                            title: '提示',
                            message: '删除成功！',
                            type: 'success'
                        });
                        this.getData();
                    }else{
                        this.$notify({
                            title: '提示',
                            message: '部分文件删除失败！',
                            type: 'error'
                        });
                        this.getData();
                    }
                    this.loading=false;
                    this.getData();
                })
            },
            //选中目标文件夹ID
            removeOnSelectChange(selectNode){
                this.removeFileOfDirectoryId = selectNode[0].id;
            },
            removebFile() { //移动
                if(this.selectFileList.length==0){
                    this.$Modal.info({
                        title: '提示',
                        content: '至少选择一条数据'
                    });
                }else{
                    this.modalRemove=true
                }
            },
            removetFile() { //移动


                var allSize = this.selectFileList.length;
                var ids ="";
                for(var i=0;i<allSize;i++) {
                    ids=ids+","+this.selectFileList[i].id ;
                }
                this.$axios.post('mvc/updateFileDirectoryId', {
                    ids: ids,
                    directoryId:this.removeFileOfDirectoryId
                }).then(res => {
                    if(res.data.success){
                        this.$notify({
                            title: '提示',
                            message: '移动成功！',
                            type: 'success'
                        });
                        this.getData();
                    }else{
                        this.$notify({
                            title: '提示',
                            message: '移动失败！',
                            type: 'error'
                        });
                    }
                })
            },
            //文件下载
            downloadFile(id) {
                this.$axios.post('mvc/downloadFile', {
                    id: id
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
            //解析
            handleParse(file) {
                // this.parserInfo.path = fileparser.name;
                let fileJson = JSON.stringify(file)
                let newFile = JSON.parse(fileJson)
                this.currentFile = newFile ? newFile : { name: '' };
                this.getParsers(file.classId);

                if(file.recommendParserId){
                    this.$axios.post('mvc/fileParserJar/getJarClassParamListById', {
                          recommendParserId:file.recommendParserId
                     }).then(res => {
                         this.parserExtList = res.data;
                      });
                 }


                /*this.$axios.post('mvc/listColumns', {
                    tableName: 'WL_JBSX'
                }).then(res => {
                    if (res.data) {
                        res.data.forEach(c => {
                            this.tableColumnData.push({
                                name: c
                            })
                        })
                    }
                })*/
                this.parserVisible = true;
            },
            //解析窗口，解析器下拉列表
            getParsers(classId) {

                this.$axios.post('mvc/fileParser/getOrderList', {
                    id: classId
                }).then(res => {
                    this.parserData = res.data ? res.data : [];
                });
               // this.parserExtList=[
               //    {parameterName:"File",parameterDesc:"文件上传",parameterValue:"1111"},
               //    {parameterName:"参数1",parameterDesc:"参数描述1",parameterValue:"1111"},
               //    {parameterName:"参数2",parameterDesc:"参数描述2",parameterValue:"111"},
               //   {parameterName:"参数3",parameterDesc:"参数描述3",parameterValue:"111"},
               //    {parameterName:"参数4",parameterDesc:"参数描述4",parameterValue:"111"},

               // ]
            },
            //关闭解析页面，初始化数据
            changeParserVisible(val) {
                if (!val) {
                    Bus.$emit('cleanData')
                }
            },



        },
        mounted() {
            this.$nextTick(() => {
                window.uploader = this.$refs.uploader.uploader
            })
        }
    }
</script>
