<template>
    <section class="main">
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon-lx-copy"></i> 多文件解析</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container" style="padding: 2px">
            <div class="drag-box">
                <div class="drag-box-item">
                    <div class="item-title">待处理</div>
                    <Progress :percent="parsePercent" />
                    <Button type="success" shape="circle" ghost size="small" style="margin: 5px" @click="handleMultiParse">执行解析</Button>
                    <Button type="success" shape="circle" ghost size="small" style="margin: 5px" @click="isFileUp=true">文件上传</Button>
                    <Card title="预分类">
                        数据库类文件
                        <Table highlight-row ref="currentRowTable1" :data="preClassDataFor1" :columns="preClassColumns1" min-height="300" height="400" tref="1"   @on-row-click="handleLeftRowClick"  @on-selection-change="handlePreSelectionChange1"></Table>
                        拓扑结构类文件
                        <Table highlight-row ref="currentRowTable1" :data="preClassDataFor2" :columns="preClassColumns2" min-height="300" height="400" tref="1"   @on-row-click="handleLeftRowClick"  @on-selection-change="handlePreSelectionChange2"></Table>
                        结构化文件
                        <Table highlight-row ref="currentRowTable1" :data="preClassDataFor3" :columns="preClassColumns3" min-height="300" height="400" tref="1"   @on-row-click="handleLeftRowClick"  @on-selection-change="handlePreSelectionChange3"></Table>
                        文本类文件
                        <Table highlight-row ref="currentRowTable1" :data="preClassDataFor4" :columns="preClassColumns4" min-height="300" height="400" tref="1"   @on-row-click="handleLeftRowClick"  @on-selection-change="handlePreSelectionChange4"></Table>
                        配置类文件
                        <Table highlight-row ref="currentRowTable1" :data="preClassDataFor5" :columns="preClassColumns5" min-height="300" height="400" tref="1"   @on-row-click="handleLeftRowClick"  @on-selection-change="handlePreSelectionChange5"></Table>
                        日志类文件
                        <Table highlight-row ref="currentRowTable1" :data="preClassDataFor6" :columns="preClassColumns6" min-height="300" height="400" tref="1"   @on-row-click="handleLeftRowClick"  @on-selection-change="handlePreSelectionChange6"></Table>
                        程序类文件
                        <Table highlight-row ref="currentRowTable1" :data="preClassDataFor7" :columns="preClassColumns7" min-height="300" height="400" tref="1"   @on-row-click="handleLeftRowClick"  @on-selection-change="handlePreSelectionChange7"></Table>
                    </Card>
                    <Card title="待分类">
                        <Table highlight-row ref="currentRowTable2" :data="waitClassData" :columns="waitClassColumns" min-height="300"  height="400" tref="2"  @on-row-click="handleLeftRowClick" @on-selection-change="handleWaitSelectionChange"></Table>
                    </Card>
                    <Card title="未分类">
                        <Table highlight-row ref="currentRowTable3" :data="otherData" :columns="otherColumns" min-height="300" height="400" tref="3"  @on-row-click="handleLeftRowClick" @on-selection-change="handleOtherSelectionChange"></Table>
                    </Card>
                </div>
                <!--<div class="drag-box-item">
                    <div class="item-title">结果<p>文件名：{{ viewFileName }}</p></div>
                   <div ref="result"></div>
                </div>-->
                <div class="drag-box-item">
                    <div class="item-title">处理后</div>
                    <Button type="success" shape="circle" ghost size="small" style="margin: 5px" @click="handleExport">执行导入</Button>
                    <Card title="预分类">
                        <Table highlight-row ref="currentRowTable4"  :data="afterPreClassData" :columns="afterPreClassColumns" @on-row-click="handleRightRowClick" tref="4" height="400" min-height="300" @on-selection-change="handleAfterPreSelectionChange"></Table>
                    </Card>
                    <Card title="待分类">
                        <Table highlight-row ref="currentRowTable5" :data="afterWaitClassData" :columns="afterWaitClassColumns" @on-row-click="handleRightRowClick" tref="5" height="400" min-height="300" @on-selection-change="handleAfterWaitSelectionChange"></Table>
                    </Card>
                    <Card title="未分类">
                        <Table highlight-row ref="currentRowTable6" :data="afterOtherData" :columns="afterOtherColumns" @on-row-click="handleRightRowClick" tref="6" height="400" min-height="300" @on-selection-change="handleAfterOtherSelectionChange"></Table>
                    </Card>
                </div>
            </div>

        </div>

        <Modal v-model="fixCon" title="解析结果">
                  <div class="drag-box-item">
                      <div class="item-title">结果<p>文件名：{{ viewFileName }}</p></div>
                      <div ref="result"></div>
                  </div>
                 </Modal>

        <Modal v-model="parserVisible" title="修改解析器" @on-ok="handleOk">
            <Select v-model="currentParser">
                <Option v-for="(item,key) in parsers" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
        </Modal>
        <Modal v-model="isFileUp" title="上传文件">
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
            </div>
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
            </uploader>
        </Modal>
    </section>
</template>

<script>
    import fileMd5 from 'browser-md5-file';
    export default {
        name: 'draglist',
        data() {
            return {
                config: {},
                fixCon:false,
                viewFileName:'',
                //解析前预分类数据
                preClassData: [],
                preClassDataFor1: [],
                preClassDataFor2: [],
                preClassDataFor3: [],
                preClassDataFor4: [],
                preClassDataFor5: [],
                preClassDataFor6: [],
                preClassDataFor7: [],
                //解析前待分类数据
                waitClassData: [],
                //解析前其他分类数据
                otherData: [],
                //当前操作下标
                currentIndex: -1,
                //当前分类
                currentType: '',
                //当前解析器
                currentParser: '',
                //解析后预分类数据
                afterPreClassData: [],
                //解析后待分类数据
                afterWaitClassData: [],
                //解析后其他分类数据
                afterOtherData: [],
                //解析前预分类选中行
                preSelection1: [],
                preSelection2: [],
                preSelection3: [],
                preSelection4: [],
                preSelection5: [],
                preSelection6: [],
                preSelection7: [],
                //解析前待分类选中行
                waitSelection: [],
                //解析前其他分类选中行
                otherSelection: [],
                afterPreSelection: [],
                //解析前待分类选中行
                afterWaitSelection: [],
                //解析前其他分类选中行
                afterOtherSelection: [],
                //所有解析器
                parsers: [],
                //解析器弹框标示
                parserVisible: false,
                //已解析数量
                parsed:0,
                //总解析数量
                allParse:0,
                //是否显示文件上传窗口
                isFileUp:false,
                tDirectoryId:0,//当前选中目录树node节点id
                tDirectoryText:'',//当前选中目录树节点名称
                treeData: [],//目录树列表
                defaultProps: {//目录树默认读取参数
                    children: 'children',
                    label: 'label'
                },
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
                //解析前预分类列
                preClassColumns1: [
                    {
                        type: 'selection',
                        width: 50,
                        align: 'center'
                    },
                    {
                        title: '文件名',
//                        width: 80,
                        key: 'name'
                    },
                    {
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '预分类1';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                preClassColumns2: [
                    {
                        type: 'selection',
                        width: 50,
                        align: 'center'
                    },
                    {
                        title: '文件名',
//                        width: 80,
                        key: 'name'
                    },
                    {
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '预分类2';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                preClassColumns3: [
                    {
                        type: 'selection',
                        width: 50,
                        align: 'center'
                    },
                    {
                        title: '文件名',
//                        width: 80,
                        key: 'name'
                    },
                    {
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '预分类3';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                preClassColumns4: [
                    {
                        type: 'selection',
                        width: 50,
                        align: 'center'
                    },
                    {
                        title: '文件名',
//                        width: 80,
                        key: 'name'
                    },
                    {
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '预分类4';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                preClassColumns5: [
                    {
                        type: 'selection',
                        width: 50,
                        align: 'center'
                    },
                    {
                        title: '文件名',
//                        width: 80,
                        key: 'name'
                    },
                    {
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '预分类5';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                preClassColumns6: [
                    {
                        type: 'selection',
                        width: 50,
                        align: 'center'
                    },
                    {
                        title: '文件名',
//                        width: 80,
                        key: 'name'
                    },
                    {
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '预分类6';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                preClassColumns7: [
                    {
                        type: 'selection',
                        width: 50,
                        align: 'center'
                    },
                    {
                        title: '文件名',
//                        width: 80,
                        key: 'name'
                    },
                    {
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '预分类7';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                //解析器待分类列
                waitClassColumns: [
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
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                        //alert(waitClassData[params.index].type);
                                         this.$axios.post('mvc/fileParser/getListForWaitClass?fileSuffix=' + this.waitClassData[params.index].type, {
                                                                                                 }).then(res => {
                                                                                                     this.parsers = res.data;
                                                                                                     });
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '待分类';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                //解析器其他分类列
                otherColumns: [
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
                        title: '解析器',
                        width: 80,
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
//                        width: 80,
                        key: 'recommendParserName',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = '其他';
                                            this.parserVisible = true;
                                        }
                                    }
                                }, '选择解析器')
                            ]);
                        }
                    }
                ],
                //预分类解析后列
                afterPreClassColumns: [
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
                        title: '解析器',
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            if(params.row.isParser!=1){
                                                let temp=[];
                                                this.afterPreClassData.forEach(item=>{
                                                    if(item.id!=params.row.id){
                                                        temp.push(item);
                                                    }
                                                })
                                                this.afterPreClassData=temp;
                                            }
                                        }
                                    }
                                }, '取消解析')
                            ]);
                        }
                    }
                ],
                //待分类解析后列
                afterWaitClassColumns: [
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
                        title: '解析器',
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            if(params.row.isParser!=1){
                                                let temp=[];
                                                this.afterWaitClassData.forEach(item=>{
                                                    if(item.id!=params.row.id){
                                                        temp.push(item);
                                                    }
                                                })
                                                this.afterWaitClassData=temp;
                                            }
                                        }
                                    }
                                }, '取消解析')
                            ]);
                        }
                    }
                ],
                //未分类解析后列
                afterOtherColumns: [
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
                        title: '解析器',
                        key: 'recommendParserName'
                    },
                    {
                        title: '操作',
                        render: (h, params) => {
                            return h('div', [
                                h('a', {
                                    on: {
                                        click: () => {
                                            event.stopPropagation()
                                            if(params.row.isParser!=1){
                                                let temp=[];
                                                this.afterOtherData.forEach(item=>{
                                                    if(item.id!=params.row.id){
                                                        temp.push(item);
                                                    }
                                                })
                                                this.afterOtherData=temp;
                                            }
                                        }
                                    }
                                }, '取消解析')
                            ]);
                        }
                    }
                ]
            }
        },
        created() {
            this.$axios.post('mvc/getConfig').then(res => {
                this.config = res.data;
            });
            this.getAllData();
            this.getTreeDate();
        },
        computed:{
            parsePercent(){
                let percent=0;
                if(this.parsed==0||this.allParse==0){
                    percent=0;
                }else{
                    percent=(this.parsed/this.allParse).toFixed(2)*100;
                }
                return percent;
            }
        },
        methods: {
            //上传合并
            fileSuccess(rootFile, file, message, chunk) {
                this.$axios.post('mvc/mergeFile', {
                    filename: file.name,
                    identifier: file.uniqueIdentifier,
                    totalSize: file.size,
                    type: file.type,
                    location: rootFile.path,
                    webkitRelativePath: file.file.webkitRelativePath,
                    directoryId: this.tDirectoryId,
                }).then(res => {
                    this.getAllData();
                }).catch(error => {
                });
            },
            // 一个根文件（文件夹）成功上传完成。
            fileComplete() {

            },
            // 上传完成
            complete() {
            },
            //文件md5
            preprocess(chunk){
                if (chunk.file.md5 === '' || chunk.file.md5 == null) {
                    fileMd5(chunk.file.file, function (err, md5) {
                        chunk.file.uniqueIdentifier=md5;
                        chunk.preprocessFinished()
                    })
                } else {
                    chunk.preprocessFinished()
                }
            },
            //递归格式化数据
            convert(childNodes){
                for (var i = 0; i < childNodes.length; i++) {
                    childNodes[i].label = childNodes[i].text;
                    childNodes[i].title = childNodes[i].text;
                    if (childNodes[i].children) {
                        this.convert(childNodes[i].children)
                    }
                }
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
            //分类管理
            handleNodeClick(data) {
                this.current=1;
                this.tDirectoryId=data.id;
                this.tDirectoryText=data.text;
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
            //文件上传
            fileUpload(){
                if(this.tDirectoryId==0 || this.tDirectoryText==''){
                    this.$Modal.info({
                        title: '提示',
                        content: '请选则一个目录！'
                    });
                }else{
                    this.isFileUp = true;
                }

            },
            //获取页面table数据
            getAllData() {
                this.$axios.post('mvc/fileParser/getList', {

                }).then(res => {
                    this.parsers = res.data;
                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '预分类',
                        'fatherClassName': '1',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.preClassDataFor1 = res.data.list;
                        for(var i=0;i<this.preClassDataFor1.length;i++){
                            this.preClassDataFor1[i]._checked=true;
                        }
                        this.handlePreSelectionChange1(this.preClassDataFor1);

                    })
                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '预分类',
                        'fatherClassName': '2',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.preClassDataFor2 = res.data.list;
                        for(var i=0;i<this.preClassDataFor2.length;i++){
                            this.preClassDataFor2[i]._checked=true;
                        }
                        this.handlePreSelectionChange2(this.preClassDataFor2);
                    })
                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '预分类',
                        'fatherClassName': '3',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.preClassDataFor3 = res.data.list;
                        for(var i=0;i<this.preClassDataFor3.length;i++){
                            this.preClassDataFor3[i]._checked=true;
                        }
                        this.handlePreSelectionChange3(this.preClassDataFor3);
                    })
                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '预分类',
                        'fatherClassName': '4',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.preClassDataFor4 = res.data.list;
                        for(var i=0;i<this.preClassDataFor4.length;i++){
                            this.preClassDataFor4[i]._checked=true;
                        }
                        this.handlePreSelectionChange4(this.preClassDataFor4);
                    })
                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '预分类',
                        'fatherClassName': '5',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.preClassDataFor5 = res.data.list;
                        for(var i=0;i<this.preClassDataFor5.length;i++){
                            this.preClassDataFor5[i]._checked=true;
                        }
                        this.handlePreSelectionChange5(this.preClassDataFor5);
                    })
                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '预分类',
                        'fatherClassName': '6',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.preClassDataFor6 = res.data.list;
                        for(var i=0;i<this.preClassDataFor6.length;i++){
                            this.preClassDataFor6[i]._checked=true;
                        }
                        this.handlePreSelectionChange6(this.preClassDataFor6);
                    })
                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '预分类',
                        'fatherClassName': '7',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.preClassDataFor7 = res.data.list;
                        for(var i=0;i<this.preClassDataFor7.length;i++){
                            this.preClassDataFor7[i]._checked=true;
                        }
                        this.handlePreSelectionChange7(this.preClassDataFor7);
                    })





                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '待分类',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.waitClassData = res.data.list;
                    })
                    this.$axios.post('mvc/pageFilesByIsParser', {
                        'classType': '其他',
                        'isParser':0,
                        isExport:0,
                        page:1,
                        limit:50
                    }).then(res => {
                        this.otherData = res.data.list;
                    })
                });
                //已解析数据清空
                this.afterPreClassData=[];
                this.afterWaitClassData=[];
                this.afterOtherData=[];
                //解析进度初始化
                this.parsed=0;
            },
            //执行多文件解析
            handleMultiParse() {
                this.parsed=0;
                this.allParse=this.preSelection1.length+this.preSelection2.length+this.preSelection3.length+this.preSelection4.length+this.preSelection5.length+this.preSelection6.length+this.preSelection7.length+this.waitSelection.length+this.otherSelection.length;
                if(this.allParse===0){
                    this.$notify({
                        title: '提示',
                        message: '请选择解析的数据',
                        type: 'error'
                    });
                    return;
                }
                if (this.preSelection1.length > 0) {
                    this.afterPreClassData=[];
                    this.preSelection1.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.preSelection1[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.preSelection1[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterPreClassData=this.afterPreClassData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.preSelection1[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }
 if (this.preSelection2.length > 0) {
                    this.afterPreClassData=[];
                    this.preSelection2.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.preSelection2[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.preSelection2[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterPreClassData=this.afterPreClassData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.preSelection2[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }
 if (this.preSelection3.length > 0) {
                    this.afterPreClassData=[];
                    this.preSelection3.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.preSelection3[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.preSelection3[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterPreClassData=this.afterPreClassData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.preSelection3[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }
             if (this.preSelection4.length > 0) {
                    this.afterPreClassData=[];
                    this.preSelection4.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.preSelection4[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.preSelection4[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterPreClassData=this.afterPreClassData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.preSelection4[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }
 if (this.preSelection5.length > 0) {
                    this.afterPreClassData=[];
                    this.preSelection5.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.preSelection5[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.preSelection5[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterPreClassData=this.afterPreClassData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.preSelection5[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }
 if (this.preSelection6.length > 0) {
                    this.afterPreClassData=[];
                    this.preSelection6.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.preSelection6[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.preSelection6[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterPreClassData=this.afterPreClassData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.preSelection6[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }
 if (this.preSelection7.length > 0) {
                    this.afterPreClassData=[];
                    this.preSelection7.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.preSelection7[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.preSelection7[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterPreClassData=this.afterPreClassData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.preSelection7[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }


                if (this.waitSelection.length > 0) {
                    this.afterWaitClassData=[];
                    this.waitSelection.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.waitSelection[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.waitSelection[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterWaitClassData=this.afterWaitClassData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.waitSelection[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }
                if (this.otherSelection.length > 0) {
                    this.afterOtherData=[];
                    this.otherSelection.forEach((item,index)=>{
                        this.$axios.post('mvc/fileParser/multiParse', {
                            selection: JSON.stringify([this.otherSelection[index]])
                        }).then(res => {
                            if(res.data.success===false){
                                this.$notify({
                                    title: '提示',
                                    message: '文件'+this.otherSelection[index].name+'解析失败:'+res.data.data,
                                    type: 'error'
                                });
                            }else{
                                this.afterOtherData=this.afterOtherData.concat(res.data);
                                this.parsed++;
                            }
                        }).catch(e=>{
                            this.$notify({
                                title: '提示',
                                message: '文件'+this.otherSelection[index].name+'解析失败',
                                type: 'error'
                            });
                        });
                    });
                }
                // this.getAllData();
            },
            //解析前预分类选中
            handlePreSelectionChange1(selection) {
                this.preSelection1 = selection;
            },
            handlePreSelectionChange2(selection) {
                this.preSelection2 = selection;
            },
            handlePreSelectionChange3(selection) {
                this.preSelection3 = selection;
            },
            handlePreSelectionChange4(selection) {
                this.preSelection4 = selection;
            },
            handlePreSelectionChange5(selection) {
                this.preSelection5 = selection;
            },
            handlePreSelectionChange6(selection) {
                this.preSelection6 = selection;
            },
            handlePreSelectionChange7(selection) {
                this.preSelection7 = selection;
            },

            //解析前待分类选中
            handleWaitSelectionChange(selection) {
                this.waitSelection= selection;
            },
            //解析器其他分类选中
            handleOtherSelectionChange(selection) {
                this.otherSelection = selection;
            },
            //解析后预分类选中
            handleAfterPreSelectionChange(selection) {
                this.afterPreSelection = selection;
            },
            //解析后待分类选中
            handleAfterWaitSelectionChange(selection) {
                this.afterWaitSelection= selection;
            },
            //解析后其他分类选中
            handleAfterOtherSelectionChange(selection) {
                this.afterOtherSelection = selection;
            },
            //解析器选择确认
            handleOk() {
                this.fixCon = false;
                let current = this.parsers.find(i => i.id == this.currentParser);
                if (this.currentType == '预分类1') {
                    this.preClassDataFor1[this.currentIndex].recommendParserId = current.id
                    this.preClassDataFor1[this.currentIndex].recommendParserName = current.name
                }
                else if (this.currentType == '预分类2') {
                     this.preClassDataFor2[this.currentIndex].recommendParserId = current.id
                     this.preClassDataFor2[this.currentIndex].recommendParserName = current.name
                 }
                else if (this.currentType == '预分类3') {
                     this.preClassDataFor3[this.currentIndex].recommendParserId = current.id
                     this.preClassDataFor3[this.currentIndex].recommendParserName = current.name
                 }
                else if (this.currentType == '预分类4') {
                     this.preClassDataFor4[this.currentIndex].recommendParserId = current.id
                     this.preClassDataFor4[this.currentIndex].recommendParserName = current.name
                 }
                else if (this.currentType == '预分类5') {
                     this.preClassDataFor5[this.currentIndex].recommendParserId = current.id
                     this.preClassDataFor5[this.currentIndex].recommendParserName = current.name
                 }
                else if (this.currentType == '预分类6') {
                     this.preClassDataFor6[this.currentIndex].recommendParserId = current.id
                     this.preClassDataFor6[this.currentIndex].recommendParserName = current.name
                 }
                else if (this.currentType == '预分类7') {
                     this.preClassDataFor7[this.currentIndex].recommendParserId = current.id
                     this.preClassDataFor7[this.currentIndex].recommendParserName = current.name
                 }

                else if (this.currentType == '待分类') {
                    this.waitClassData[this.currentIndex].recommendParserId = current.id
                    this.waitClassData[this.currentIndex].recommendParserName = current.name
                } else {
                    this.otherData[this.currentIndex].recommendParserId = current.id
                    this.otherData[this.currentIndex].recommendParserName = current.name
                }
            },
            //右列table rowclick事件
            handleRightRowClick(row) {
                this.highLightRow(row);
                this.$refs.result.innerHTML = '<textarea id="ID"  style="width:100%;min-height:600px;overflow:scroll;resize:none;" >'+ row.parseResult +'</textarea>'
            },
            //左列table rowclick 事件
            handleLeftRowClick(row) {
                this.highLightRow(row);
                let fileServerPath = this.config.fileServerPath;
                let previewPath = this.config.previewPath;
                let fileUrl = fileServerPath + '/' +  row.groups + '/'+ row.realPath;
                let uri = previewPath + encodeURIComponent(fileUrl);
                this.$refs.result.innerHTML= '<iframe src="'+ uri +'" height="600" width="98%"></iframe>'
            },
            highLightRow(row) {
                this.viewFileName = row.name;
                this.fixCon=true;
                if (!(row.classType == '预分类' && row.isParser == 0)) {
                    this.$refs.currentRowTable1.clearCurrentRow();
                }
                if (!(row.classType == '待分类' && row.isParser == 0)) {
                    this.$refs.currentRowTable2.clearCurrentRow();
                }
                if (!(row.classType == '其他' && row.isParser == 0)) {
                    this.$refs.currentRowTable3.clearCurrentRow();
                }
                if (!(row.classType == '预分类' && row.isParser == 1)) {
                    this.$refs.currentRowTable4.clearCurrentRow();
                }
                if (!(row.classType == '待分类' && row.isParser == 1)) {
                    this.$refs.currentRowTable5.clearCurrentRow();
                }
                if (!(row.classType == '其他' && row.isParser == 1)) {
                    this.$refs.currentRowTable6.clearCurrentRow();
                }
            },
            handleExport() {
                if(this.parsed==0){
                    this.$notify({
                        title: '提示',
                        message: '请先解析数据再导入',
                        type: 'error'
                    });
                    return;
                }
                let data=[];
                let parserData=[];
                //加入预分类解析数据
                this.afterPreClassData.forEach(item=>{
                    this.afterPreSelection.forEach(item1=>{
                        if(item1.id=item.id){
                            data.push(item.parseResult);
                            parserData.push({
                                parserId:item.recommendParserId,
                                fileId:item.id
                            })
                        }
                    })
                })
                //加入待分类解析数据
                this.afterWaitClassData.forEach(item=>{
                    this.afterWaitSelection.forEach(item1=>{
                        if(item1.id=item.id){
                            data.push(item.parseResult);
                            parserData.push({
                                parserId:item.recommendParserId,
                                fileId:item.id
                            })
                        }
                    })
                })
                //加入未分类解析数据
                this.afterOtherData.forEach(item=>{
                    this.afterOtherSelection.forEach(item1=>{
                        if(item1.id=item.id){
                            data.push(item.parseResult);
                            parserData.push({
                                parserId:item.recommendParserId,
                                fileId:item.id
                            })
                        }
                    })
                })
                if(data.length==0){
                    this.$notify({
                        title: '提示',
                        message:'请选择入库数据',
                        type: 'error'
                    });
                    return;
                }
                this.$axios.post('mvc/fileParser/multiParseSaveData', {
                    dataJSON: data.join('#'),
                    parserDataJSON:JSON.stringify(parserData)
                }).then(res => {
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                    if(res.data.success){
                        this.getAllData();
                    }
                }).catch(e=>{
                    this.$notify({
                        title: '提示',
                        message: e.response.data.errorText,
                        type: 'error'
                    });
                });
            }
        }
    }

</script>

<style scoped>
    .drag-box{
        display: flex;
        user-select: none;
    }
    .drag-box-item {
        flex: 1;
        min-width: 300px;
        background-color: #eff1f5;
        margin-right: 5px;
        border-radius: 6px;
        border: 1px #e1e4e8 solid;
    }
    .item-title{
        padding: 8px 8px 8px 12px;
        font-size: 14px;
        line-height: 1.5;
        color: #24292e;
        font-weight: 600;
    }
    .demo-spin-icon-load{
        animation: ani-demo-spin 1s linear infinite;
    }
    .ivu-spin-fix {
        top: 30px;
    }
</style>
