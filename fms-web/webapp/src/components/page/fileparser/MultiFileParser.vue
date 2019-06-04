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
                    <el-row>
                        <el-col :span="15">
                            <div :class="{'title-fixed': needFixed}" class="item-title">待处理</div>
                        </el-col>
                        <el-col :span="4">
                            <div class="item-title-sel">每页显示</div>
                        </el-col>
                        <el-col :span="3">

                            <Select v-model="numOfSelect" >
                                <Option v-for="(item,key) in numOfSelectOptions" :value="item" :key="item">{{ item }}
                                </Option>
                            </Select>

                        </el-col>
                        <el-col :span="2">
                            <div class="item-title-sel">条</div>
                        </el-col>
                    </el-row>

                    <Progress :percent="parsePercent"/>
                    <Button type="success" shape="circle" ghost size="small" style="margin: 5px"
                            @click="handleMultiParse">执行解析
                    </Button>
                    <Button type="success" shape="circle" ghost size="small" style="margin: 5px" @click="isFileUp=true">
                        文件上传
                    </Button>
                    <Card title="预分类">
                        <div style="cursor:pointer" @click="choseDbFile">数据库类文件 <i v-show="!dBshow" data-v-1f234af1=""
                                                                                   class="el-icon-caret-right"></i> <i
                            v-show="dBshow" data-v-1f234af1="" class="el-icon-caret-bottom"></i></div>
                        <div v-show="dBshow">
                            <Table highlight-row ref="currentRowTable1" :data="preClassDataFor1"
                                   :columns="preClassColumns1" min-height="300" height="400" tref="1"

                                   @on-selection-change="handlePreSelectionChange1"></Table>
                        </div>
                        <div style="border:1px dashed #c0c4cc"></div>
                        <div style="cursor:pointer" @click="choseTbFile">拓扑结构类文件 <i v-show="!tPshow" data-v-1f234af1=""
                                                                                    class="el-icon-caret-right"></i> <i
                            v-show="tPshow" data-v-1f234af1="" class="el-icon-caret-bottom"></i></div>
                        <div v-show="tPshow">
                            <Table highlight-row ref="currentRowTable1" :data="preClassDataFor2"
                                   :columns="preClassColumns2" min-height="300" height="400" tref="1"

                                   @on-selection-change="handlePreSelectionChange2"></Table>
                        </div>
                        <div style="border:1px dashed #c0c4cc"></div>
                        <div style="cursor:pointer" @click="choseJgFile">结构化文件 <i v-show="!jGshow" data-v-1f234af1=""
                                                                                  class="el-icon-caret-right"></i> <i
                            v-show="jGshow" data-v-1f234af1="" class="el-icon-caret-bottom"></i></div>
                        <div v-show="jGshow">
                            <Table highlight-row ref="currentRowTable1" :data="preClassDataFor3"
                                   :columns="preClassColumns3" min-height="300" height="400" tref="1"

                                   @on-selection-change="handlePreSelectionChange3"></Table>
                        </div>
                        <div style="border:1px dashed #c0c4cc"></div>
                        <div style="cursor:pointer" @click="choseTxFile">文本类文件 <i v-show="!tXshow" data-v-1f234af1=""
                                                                                  class="el-icon-caret-right"></i> <i
                            v-show="tXshow" data-v-1f234af1="" class="el-icon-caret-bottom"></i></div>
                        <div v-show="tXshow">
                            <Table highlight-row ref="currentRowTable1" :data="preClassDataFor4"
                                   :columns="preClassColumns4" min-height="300" height="400" tref="1"

                                   @on-selection-change="handlePreSelectionChange4"></Table>
                        </div>
                        <div style="border:1px dashed #c0c4cc"></div>
                        <div style="cursor:pointer" @click="choseCfFile">配置类文件 <i v-show="!cFshow" data-v-1f234af1=""
                                                                                  class="el-icon-caret-right"></i> <i
                            v-show="cFshow" data-v-1f234af1="" class="el-icon-caret-bottom"></i></div>
                        <div v-show="cFshow">
                            <Table highlight-row ref="currentRowTable1" :data="preClassDataFor5"
                                   :columns="preClassColumns5" min-height="300" height="400" tref="1"

                                   @on-selection-change="handlePreSelectionChange5"></Table>
                        </div>
                        <div style="border:1px dashed #c0c4cc"></div>
                        <div style="cursor:pointer" @click="choseLgFile">日志类文件 <i v-show="!lGshow" data-v-1f234af1=""
                                                                                  class="el-icon-caret-right"></i> <i
                            v-show="lGshow" data-v-1f234af1="" class="el-icon-caret-bottom"></i></div>
                        <div v-show="lGshow">
                            <Table highlight-row ref="currentRowTable1" :data="preClassDataFor6"
                                   :columns="preClassColumns6" min-height="300" height="400" tref="1"

                                   @on-selection-change="handlePreSelectionChange6"></Table>
                        </div>
                        <div style="border:1px dashed #c0c4cc"></div>
                        <div style="cursor:pointer" @click="chosePrFile">程序类文件 <i v-show="!pRshow" data-v-1f234af1=""
                                                                                  class="el-icon-caret-right"></i> <i
                            v-show="pRshow" data-v-1f234af1="" class="el-icon-caret-bottom"></i></div>
                        <div v-show="pRshow">
                            <Table highlight-row ref="currentRowTable1" :data="preClassDataFor7"
                                   :columns="preClassColumns7" min-height="300" height="400" tref="1"

                                   @on-selection-change="handlePreSelectionChange7"></Table>
                        </div>
                        <div style="border:1px dashed #c0c4cc"></div>
                    </Card>
                    <Card title="待分类">
                        <Table highlight-row ref="currentRowTable2" :data="waitClassData" :columns="waitClassColumns"
                               min-height="300" height="400" tref="2"
                               @on-selection-change="handleWaitSelectionChange"></Table>
                    </Card>
                    <Card title="未分类">
                        <Table highlight-row ref="currentRowTable3" :data="otherData" :columns="otherColumns"
                               min-height="300" height="400" tref="3"
                               @on-selection-change="handleOtherSelectionChange"></Table>
                    </Card>
                </div>
                <!--<div class="drag-box-item">
                    <div class="item-title">结果<p>文件名：{{ viewFileName }}</p></div>
                   <div ref="result"></div>
                </div>-->
                <div class="drag-box-item">
                    <div :class="{'title-fixed': needFixed}" class="item-title">处理后</div>
                    <Button type="success" shape="circle" ghost size="small" style="margin: 5px" @click="handleExport">
                        执行导入
                    </Button>
                    <Card title="预分类">
                        <Table highlight-row ref="currentRowTable4" :data="afterPreClassData"
                               :columns="afterPreClassColumns" @on-row-click="handleRightRowClick" tref="4" height="400"
                               min-height="300" @on-selection-change="handleAfterPreSelectionChange"></Table>
                    </Card>
                    <Card title="待分类">
                        <Table highlight-row ref="currentRowTable5" :data="afterWaitClassData" :columns="afterWaitClassColumns"
                               @on-row-click="handleRightRowClick" tref="5" height="400" min-height="300"
                               @on-selection-change="handleAfterWaitSelectionChange"></Table>
                    </Card>
                    <Card title="未分类">
                        <Table highlight-row ref="currentRowTable6" :data="afterOtherData" :columns="afterOtherColumns"
                               @on-row-click="handleRightRowClick" tref="6" height="400" min-height="300"
                               @on-selection-change="handleAfterOtherSelectionChange"></Table>
                    </Card>
                </div>
            </div>

        </div>

        <Modal v-model="fixCon" @on-cancel="cancel11" title="解析结果" width="80%" >
            <!--<i-col span="4">-->
                <!--<Button @click="handlePreview">预览拓扑图</Button>-->
            <!--</i-col>-->
            <div class="drag-box-item">
                <div class="item-title">结果<p>文件名：{{ viewFileName }}</p></div>
                <div ref="result"></div>
            </div>
            <!--表格tab-->
            <Row>
                <Col span="24">
                    <div class="tabs" style="height:20px">
                        <div
                            style="float: left;margin-right: 10px; width: 100px;float: left; margin: 3px 5px 2px 3px;border-radius: 3px;font-size: 12px;overflow: hidden;cursor: pointer;height: 23px;line-height: 23px;border: 1px solid #e9eaec;background: #fff;padding: 0 5px 0 12px;vertical-align: middle;color: #666;transition: all .3s ease-in;"
                            :class="{tableActive:index == tableIndex}" v-for="(value,key,index) in jsonTables"
                            @click="toggleTab(index)"><a>{{key}}</a></div>
                    </div>
                </Col>
                <Col span="24">
                    <div v-if="jsonTables && Object.keys(jsonTables).length" style="height: 300px; overflow-y: auto;">
                        <table style="border-right:1px solid #ddd;border-bottom:1px solid #ddd;width:100%;overflow-x:scroll;"
                               v-for="(value,key,index) in jsonTables" :key="key" :label="value" :value="key"
                               v-show="index == tableIndex" border="0" cellspacing="0" cellpadding="0">
                            <thead>
                            <tr>
                                <th style="background: #c0c4cc;border: 1px solid #ddd;" v-for="keyvalue in allKeyAll[index]">
                                    {{keyvalue}}
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr v-for="(value, key) in [...value]">

                                <td v-for="keyvalue in allKeyAll[index]"
                                    style="text-align: center;border-left:1px solid #ddd;border-top:1px solid #ddd">
                                    {{value[keyvalue]}}
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </Col>
            </Row>

            <!--改表格高度-->




            <div>
                <Form v-if="columnData && Object.keys(columnData).length" style="height: 100px">
                    <FormItem inline :label-width="100" label="请选择模板">
                        <Select
                                @on-change="(templateName) => onChangeTemplate(templateName)"
                                style="width: 180px"
                                v-model="selectTemplateName"
                                :clearable="true"
                        >
                            <Option v-for="template in  this.templateNameInfos" :value="template.templateName" :key="template.templateName"> {{ template.templateName }}</Option>
                        </Select>
                    </FormItem>
                </Form>
            </div>
            <!--选择表和字段-->
            <div v-if="columnData && Object.keys(columnData).length" style="height: 300px; overflow-y: auto;">
                <Form inline v-for="(data,key) in columnData" :key="key" v-if="columnKeyNamesMap[key] != null">

                    <FormItem :label-width="100" :label="key"></FormItem>

                    <FormItem label="选择库">
                        <Select
                                style="width: 180px"
                                @on-change="(schemaId) =>getTables(schemaId, key)"

                                v-model="columnKeyNamesMap[key].schemaId"
                                :clearable="true"
                        >
                            <Option v-for="(schema,schemaIdx) in columnSelectMap[key].schemas" :value="schema.id" :key="schemaIdx"> {{ schema.name }}</Option>
                        </Select>
                    </FormItem>

                    <FormItem label="选择表">

                        <Select
                                @on-change="(tableId) => getColumnsByTable(tableId, key)"
                                style="width: 180px"
                                v-model="columnKeyNamesMap[key].tableId"
                                :clearable="true"
                        >
                            <Option v-for="(table,tableIdx) in columnSelectMap[key].tables" :value="table.id" :data="table.id" :key="table.id"> {{ table.tableChinese }}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="选择字段" :data="columnKeyNamesMap[key].columnId">

                        <Select
                                @on-change="(columnId) => getDicByColumn(columnId, key)"
                                style="width: 180px"
                                v-model="columnKeyNamesMap[key].columnId+''"
                                :clearable="true"
                        >
                            <Option v-for="(column,columnIdx) in columnSelectMap[key].columns" :data="column.id" :value="column.id+''" :key="column.id+''"> {{ column.columnChinese }}</Option>
                        </Select>
                    </FormItem>
                    <template v-if="columnSelectMap[key] && columnSelectMap[key].dicTables">
                        <FormItem v-for="dicTable in columnSelectMap[key].dicTables" :label="dicTable.columnChinese">
                            <Select
                                    style="width: 180px"
                                    :data="columnKeyNamesMap[key]['dicMap'][dicTable.columnEnglish]"
                                    v-model="columnKeyNamesMap[key]['dicMap'][dicTable.columnEnglish]"
                                    :clearable="true"
                                >
                                <Option v-for="(dic,dicIdx) in dicTable.dicList" :data="dic.dm" :value="dic.dm+''" :key="dic.dm+''"> {{ dic.mc }}</Option>
                            </Select>
                        </FormItem>
                    </template>
                </Form>
            </div>
            <Modal
                title="请输入模板名称"
                v-model="templateNameVisible"
                @on-ok="saveTemplate(2)"
                :mask-closable="false">
                <FormItem inline :label-width="100" label="请输入模板名称">
                    <Input v-model="newTemplateName"></Input>
                </FormItem>
            </Modal>
                <div ref="table" v-show="false"></div>
                <Button type="primary" @click="saveTemplate(1)">保存映射关系到原模板</Button>
                <Button type="primary" @click="saveTemplateToNew">保存映射关系到新模板</Button>
            <div slot="footer"></div>
        </Modal>

        <Modal v-model="parserVisible" title="修改解析器" @on-ok="handleOk">
            <Select v-model="currentParser" >
                <Option v-for="(item,key) in parsers" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
        </Modal>
        <Modal v-model="isFileUp" title="上传文件">
            <div class="tree">
                <el-tree :data="treeData" :props="defaultProps" @node-click="handleNodeClick"
                         :expand-on-click-node="false"
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

                    <uploader-btn>选择文件</uploader-btn>
                    <uploader-btn :directory="true">选择文件夹</uploader-btn>
                </uploader-drop>
                <uploader-list></uploader-list>
            </uploader>
        </Modal>
        <Modal v-model="setVisible" title="多参数设置" @on-ok="handleSetOk">
            <Form ref="formCustom">
                <FormItem  v-for="item in fileCountMap[currentFileId]"  :label="item.parameterDesc">
                    <Input v-if="item.parameterName != 'File' && item.parameterName != 'boolean'" v-model="item.parameterValue" :placeholder="item.parameterDesc"/>
                    <Select v-if="item.parameterName == 'boolean'" v-model="item.parameterValue" >
                        <Option value="1"  >是</Option>
                        <Option value="0"  >否</Option>
                    </Select>
                    <div v-if="item.parameterName == 'File'">
                        <Upload
                            ref="uploadFile"
                            multiple
                            :before-upload="handleUploadFile"
                            action="mvc/fileParserJar/uploadFileParam"
                            :on-success="uploadSuccessFile"
                            :on-remove="handleRemoveFile"
                            :on-error="uploadErrorFile">
                            <Button icon="ios-cloud-upload-outline">上传</Button>
                            <span>{{resultFils ? resultFils : item.parameterValue}}</span>
                        </Upload>
                        <Button style="margin-top: 5px" type="text" @click="FileUpload" :loading="loadingStatus">{{ loadingStatus ? '上传中' : '上传' }}</Button>
                    </div>
                </FormItem>
            </Form>
        </Modal>
    </section>
</template>

<script>
    import fileMd5 from "browser-md5-file";
    export default {
        watch: {
            selectTemplateName(){

            }
        },



        name: "draglist",
        data() {
            return {
                templateNameVisible:false,
                needFixed: false,
                idPropertiesMap: {},
                idPropertiesMapTemp: {},
                dBshow: false,
                tPshow: false,
                jGshow: false,
                tXshow: false,
                cFshow: false,
                lGshow: false,
                pRshow: false,
                config: {},
                fixCon: false,
                viewFileName: "",
                table_name: '',

                currentFileId: '',
                //文件详情
                fileCountMap: {},
                //表中所有字段属性
                allTableField: [],
                initHandleOkState:1, // 条件是否满足执行hanlerOk事件
                preClassColumnsParams: null,
                index11:1,

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
                //模板信息
                templateNameInfos:[],
                selectTemplateName:'',
                //解析前其他分类数据
                otherData: [],
                //当前操作下标
                currentIndex: -1,
                //当前分类
                currentType: "",

                //当前解析器
                currentParser: "",
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
                columnData: {},
                //所有解析器
                parsers: [],
                //解析器弹框标示
                parserVisible: false,
                // 多参数设置弹窗
                setVisible: false,
                //已解析数量
                parsed: 0,
                //新模板名称
                newTemplateName:'',
                //总解析数量
                allParse: 0,
                //是否显示文件上传窗口
                isFileUp: false,
                tDirectoryId: 0, //当前选中目录树node节点id
                tDirectoryText: "", //当前选中目录树节点名称
                treeData: [], //目录树列表
                defaultProps: {
                    //目录树默认读取参数
                    children: "children",
                    label: "label"
                },
                options: {
                    //文件分片上传数据配置
                    target: process.env.BASE_UPLOAD + "mvc/chunk",
                    testChunks: true,
                    simultaneousUploads: 1,
                    preprocess: this.preprocess,
                    chunkSize: 1024 * 1024 * 5
                },
                statusText: {
                    success: "成功了",
                    error: "出错了",
                    uploading: "上传中",
                    paused: "暂停中",
                    waiting: "等待中"
                },
                //解析前预分类列
                preClassColumns1: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        //                        width: 80,
                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        key: "recommendParserName",
                        render: (h, params) => {
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            this.preClassColumnsParams = params;
                            const hasMulte =
                                this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length;
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            //alert(waitClassData[params.index].type);
                                            // this.$axios.post("mvc/fileParser/getList", {}).then(res => {
                                            //     this.parsers = res.data;
                                            // });
                                            // event.stopPropagation();
                                            // this.currentIndex = params.index;
                                            // this.currentParser = params.row.recommendParserId;
                                            // this.currentType = "预分类1";
                                            // this.fixCon = true;
                                            // let fileServerPath = this.config.fileServerPath;
                                            // let previewPath = this.config.previewPath;
                                            // let fileUrl = fileServerPath + "/" + row.groups + "/" + row.realPath;
                                            // let uri = previewPath + encodeURIComponent(fileUrl);
                                            // this.$refs.result.innerHTML =
                                            //     '<iframe src="' + uri + '" height="600" width="98%"></iframe>';
                                            this.preview(row);

                                        }
                                    }
                                },"预览 |"),
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                this.$axios
                                                    .post("mvc/fileParser/getList", {})
                                                    .then(res => {
                                                        this.parsers = res.data;
                                                    });
                                                event.stopPropagation();
                                                this.currentIndex = params.index;
                                                this.currentParser = params.row.id;
                                                this.currentType = "预分类1";
                                                this.parserVisible = true;
                                            }
                                        }
                                    },
                                    " 选择解析器 "),
                                hasMulte? h("a",{
                                    on: {
                                        click: () => {
                                            this.currentIndex = params.index;
                                            this.currentFileId = params.row.id;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = "预分类1";
                                            this.setVisible = true;
                                        }
                                    }
                                },
                                "| 多参数设置")
                                : null
                            ]);
                        }
                    }
                ],
                preClassColumns2: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",

                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        key: "recommendParserName",
                        render: (h, params) => {
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            this.preClassColumnsParams = params;
                            const hasMulte =
                                (this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length);
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.preview(row);
                                        }
                                    }
                                },"预览 |"),
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                this.$axios
                                                    .post("mvc/fileParser/getList", {})
                                                    .then(res => {
                                                        this.parsers = res.data;
                                                    });
                                                event.stopPropagation();
                                                this.currentIndex = params.index;
                                                this.currentParser = params.row.recommendParserId;
                                                this.currentType = "预分类2";
                                                this.parserVisible = true;
                                            }
                                        }
                                    }," 选择解析器 "),
                                hasMulte? h("a",{
                                    on: {
                                        click: () => {
                                            this.currentIndex = params.index;
                                            this.currentFileId = params.row.id;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = "预分类2";
                                            this.setVisible = true;
                                        }
                                    }
                                },"| 多参数设置"): null
                            ]);
                        }
                    }
                ],
                preClassColumns3: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        //                        width: 80,
                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        key: "recommendParserName",
                        render: (h, params) => {
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            this.preClassColumnsParams = params;

                            const hasMulte =
                            this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length;
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.preview(row);
                                        }
                                    }
                                },"预览 |"),
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                this.$axios
                                                    .post("mvc/fileParser/getList", {})
                                                    .then(res => {
                                                        this.parsers = res.data;
                                                    });
                                                event.stopPropagation();
                                                this.currentIndex = params.index;
                                                this.currentParser = params.row.recommendParserId;
                                                this.currentType = "预分类3";
                                                this.parserVisible = true;
                                            }
                                        }
                                    }," 选择解析器 "),
                                hasMulte? h("a",{
                                    on: {
                                        click: () => {
                                            this.currentIndex = params.index;
                                            this.currentFileId = params.row.id;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = "预分类3";
                                            this.setVisible = true;

                                        }
                                    }
                                },"| 多参数设置"): null
                            ]);
                        }
                    }
                ],
                preClassColumns4: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        //                        width: 80,
                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        key: "recommendParserName",
                        render: (h, params) => {
                            this.preClassColumnsParams = params;
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            const hasMulte =
                                this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length;
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.preview(row);
                                        }
                                    }
                                },"预览 |"),
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                this.$axios
                                                    .post("mvc/fileParser/getList", {})
                                                    .then(res => {
                                                        this.parsers = res.data;
                                                    });
                                                event.stopPropagation();
                                                this.currentIndex = params.index;
                                                this.currentParser = params.row.recommendParserId;
                                                this.currentType = "预分类4";
                                                this.parserVisible = true;
                                            }
                                        }
                                    }," 选择解析器 "),
                                hasMulte? h("a",{
                                    on: {
                                        click: () => {
                                            this.currentIndex = params.index;
                                            this.currentFileId = params.row.id;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = "预分类4";
                                            this.setVisible = true;
                                        }
                                    }
                                },"| 多参数设置"): null
                            ]);
                        }
                    }
                ],
                preClassColumns5: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        //                        width: 80,
                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        key: "recommendParserName",
                        render: (h, params) => {
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            this.preClassColumnsParams = params;
                            const hasMulte =
                                this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length;
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.preview(row);
                                        }
                                    }
                                },"预览 |"),
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                this.$axios
                                                    .post("mvc/fileParser/getList", {})
                                                    .then(res => {
                                                        this.parsers = res.data;
                                                    });
                                                event.stopPropagation();
                                                this.currentIndex = params.index;
                                                this.currentParser = params.row.recommendParserId;
                                                this.currentType = "预分类5";
                                                this.parserVisible = true;
                                            }
                                        }
                                    }," 选择解析器 "),
                                hasMulte? h("a",{
                                    on: {
                                        click: () => {
                                            this.currentIndex = params.index;
                                            this.currentFileId = params.row.id;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = "预分类5";
                                            this.setVisible = true;
                                        }
                                    }
                                },"| 多参数设置"): null
                            ]);
                        }
                    }
                ],
                preClassColumns6: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        //                        width: 80,
                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        key: "recommendParserName",
                        render: (h, params) => {
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            this.preClassColumnsParams = params;
                            const hasMulte =
                                this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length;
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.preview(row);
                                        }
                                    }
                                },"预览 |"),
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                this.$axios
                                                    .post("mvc/fileParser/getList", {})
                                                    .then(res => {
                                                        this.parsers = res.data;
                                                    });
                                                event.stopPropagation();
                                                this.currentIndex = params.index;
                                                this.currentParser = params.row.recommendParserId;
                                                this.currentType = "预分类6";
                                                this.parserVisible = true;
                                            }
                                        }
                                    }," 选择解析器 "),
                                hasMulte? h("a",{
                                    on: {
                                        click: () => {
                                            this.currentIndex = params.index;
                                            this.currentFileId = params.row.id;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = "预分类6";
                                            this.setVisible = true;
                                        }
                                    }
                                },"| 多参数设置"): null
                            ]);
                        }
                    }
                ],
                preClassColumns7: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        //                        width: 80,
                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        key: "recommendParserName",
                        render: (h, params) => {
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            this.preClassColumnsParams = params;
                            const hasMulte =
                                this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length;
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.preview(row);
                                        }
                                    }
                                },"预览 |"),
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                this.$axios
                                                    .post(
                                                "mvc/fileParser/getList?fileSuffix=" +
                                                this.waitClassData[params.index].type, {})
                                                    .then(res => {
                                                        this.parsers = res.data;
                                                    });
                                                event.stopPropagation();
                                                this.currentIndex = params.index;
                                                this.currentParser = params.row.recommendParserId;
                                                this.currentType = "预分类7";
                                                this.parserVisible = true;
                                            }
                                        }
                                    }," 选择解析器 "),
                                hasMulte? h("a",{
                                    on: {
                                        click: () => {
                                            this.currentIndex = params.index;
                                            this.currentFileId = params.row.id;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = "预分类7";
                                            this.setVisible = true;
                                        }
                                    }
                                },"| 多参数设置"): null
                            ]);
                        }
                    }
                ],
                //解析器待分类列
                waitClassColumns: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        key: "recommendParserName",
                        render: (h, params) => {
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            const hasMulte =
                                this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length;
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.preview(row);
                                        }
                                    }
                                },"预览 |"),
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                //alert(waitClassData[params.index].type);
                                                this.$axios
                                                    .post(
                                                        "mvc/fileParser/getListForWaitClass?fileSuffix=" +
                                                        this.waitClassData[params.index].type, {})
                                                    .then(res => {
                                                        this.parsers = res.data;
                                                    });
                                                event.stopPropagation();
                                                this.currentIndex = params.index;
                                                this.currentParser = params.row.recommendParserId;
                                                this.currentType = "待分类";
                                                this.parserVisible = true;
                                            }
                                        }
                                    },
                                    " 选择解析器 "
                                ),
                                hasMulte
                                    ? h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                this.currentIndex = params.index;
                                                this.currentFileId = params.row.id;
                                                this.currentParser = params.row.recommendParserId;
                                                this.currentType = "待分类";
                                                this.setVisible = true;
                                            }
                                        }
                                    },
                                    "| 多参数设置"
                                    )
                                    : null
                            ]);
                        }
                    }
                ],
                //解析器其他分类列
                otherColumns: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        key: "name"
                    },
                    {
                        title: "解析器",
                        width: 80,
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        //                        width: 80,
                        key: "recommendParserName",
                        render: (h, params) => {
                            const {row, index} = params || {};
                            const {recommendParserId} = row || {};
                            const hasMulte =
                                this.idPropertiesMap[recommendParserId] &&
                                this.idPropertiesMap[recommendParserId].length;
                            return h("div", [
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.preview(row);
                                        }
                                    }
                                },"预览 |"),
                                h("a",{
                                    on: {
                                        click: () => {
                                            this.$axios
                                                .post("mvc/fileParser/getList", {})
                                                .then(res => {
                                                    this.parsers = res.data;
                                                });
                                            event.stopPropagation();
                                            this.currentIndex = params.index;
                                            this.currentParser = params.row.recommendParserId;
                                            this.currentType = "其他";
                                            this.parserVisible = true;
                                        }
                                    }
                                }," 选择解析器 "),
                                hasMulte? h("a",{
                                    on: {
                                        click: () => {
                                            this.currentIndex = params.index;
                                            this.currentFileId = params.row.id;
                                            this.currentParser = params.row.recommendParserId;
                                            this.setVisible = true;
                                        }
                                    }
                                },"| 多参数设置"): null
                            ]);
                        }
                    }
                ],
                //预分类解析后列
                afterPreClassColumns: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        key: "name"
                    },
                    {
                        title: "解析器",
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        render: (h, params) => {
                            return h("div", [
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                event.stopPropagation();
                                                if (params.row.isParser != 1) {
                                                    let temp = [];
                                                    this.afterPreClassData.forEach(item => {
                                                        if (item.id != params.row.id) {
                                                            temp.push(item);
                                                        }
                                                    });
                                                    this.afterPreClassData = temp;
                                                }
                                            }
                                        }
                                    },
                                    "取消解析"
                                )
                            ]);
                        }
                    }
                ],
                //待分类解析后列
                afterWaitClassColumns: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        key: "name"
                    },
                    {
                        title: "解析器",
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        render: (h, params) => {
                            return h("div", [
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                event.stopPropagation();
                                                if (params.row.isParser != 1) {
                                                    let temp = [];
                                                    this.afterWaitClassData.forEach(item => {
                                                        if (item.id != params.row.id) {
                                                            temp.push(item);
                                                        }
                                                    });
                                                    this.afterWaitClassData = temp;
                                                }
                                            }
                                        }
                                    },
                                    "取消解析"
                                )
                            ]);
                        }
                    }
                ],
                //未分类解析后列
                afterOtherColumns: [
                    {
                        type: "selection",
                        width: 50,
                        align: "center"
                    },
                    {
                        title: "文件名",
                        key: "name"
                    },
                    {
                        title: "解析器",
                        key: "recommendParserName"
                    },
                    {
                        title: "操作",
                        render: (h, params) => {
                            return h("div", [
                                h(
                                    "a",
                                    {
                                        on: {
                                            click: () => {
                                                event.stopPropagation();
                                                if (params.row.isParser != 1) {
                                                    let temp = [];
                                                    this.afterOtherData.forEach(item => {
                                                        if (item.id != params.row.id) {
                                                            temp.push(item);
                                                        }
                                                    });
                                                    this.afterOtherData = temp;
                                                }
                                            }
                                        }
                                    },
                                    "取消解析"
                                )
                            ]);
                        }
                    }
                ],
                limit: 20,
                numOfSelect: 20,
                numOfSelectOptions: [20, 30, 50],
                allKey:[],//解析的文件key的数组
                allKeyAll:[],//解析的文件key的数组
                columnKeyNamesMap: {},
                columnSelectMap: {},
                schemas: [],
                tempFileId:'',//当点开预览时候把文件id临时存放在此变量中
                customKeysObj:{},//每个文件keys存储
                columnKeyNamesMapObj:{},//每个文件映射关系存储
                tableIndex: 0,
                jsonTables: {},
            };
        },
        created() {
            this.$axios.post("mvc//getConfig").then(res => {
                this.config = res.data;
            });
            this.getAllData();
            this.getTreeDate();
        },
        mounted() {
            this.scrollEl = document.querySelector(".wrapper .content-box .content");
            this.scrollEl.addEventListener("scroll", this.handleFixed);
        },
        destroyed() {
            this.scrollEl.removeEventListener("scroll", this.handleFixed);
        },
        computed: {
            parsePercent() {
                let percent = 0;
                if (this.parsed == 0 || this.allParse == 0) {
                    percent = 0;
                } else {
                    percent = (this.parsed / this.allParse).toFixed(2) * 100;
                }
                return percent;
            }
        },
        methods: {

            handleFixed(e) {
                const scrollTop = e.target.scrollTop;
                if (scrollTop > 100) {
                    this.needFixed = true;
                } else {
                    this.needFixed = false;
                }
            },
            //数据库类文件
            choseDbFile() {
                this.dBshow = !this.dBshow;
                this.initHandleOk('预分类1', this.preClassDataFor1)
            },
            //拓扑结构类文件
            choseTbFile() {
                this.tPshow = !this.tPshow;
                this.initHandleOk('预分类2',this.preClassDataFor2)
            },
            //结构化文件
            choseJgFile() {
                this.jGshow = !this.jGshow;
                this.initHandleOk('预分类3',this.preClassDataFor3)
            },
            //文本类文件
            choseTxFile() {
                this.tXshow = !this.tXshow;
                this.initHandleOk('预分类4',this.preClassDataFor4)
            },
            //配置类文件
            choseCfFile() {
                this.cFshow = !this.cFshow;
                this.initHandleOk('预分类5',this.preClassDataFor5)
            },
            //日志类文件
            choseLgFile() {
                this.lGshow = !this.lGshow;
                this.initHandleOk('预分类6',this.preClassDataFor6)
            },
            //程序类文件
            chosePrFile() {
                this.pRshow = !this.pRshow;
                this.initHandleOk('预分类7',this.preClassDataFor7)
            },
            //上传合并
            fileSuccess(rootFile, file, message, chunk) {
                this.$axios
                    .post("mvc/mergeFile", {
                        filename: file.name,
                        identifier: file.uniqueIdentifier,
                        totalSize: file.size,
                        type: file.type,
                        location: rootFile.path,
                        webkitRelativePath: file.file.webkitRelativePath,
                        directoryId: this.tDirectoryId
                    })
                    .then(res => {
                        this.getAllData();
                    })
                    .catch(error => {
                    });
            },
            // 一个根文件（文件夹）成功上传完成。
            fileComplete() {
            },
            // 上传完成
            complete() {
            },
            //文件md5
            preprocess(chunk) {
                if (chunk.file.md5 === "" || chunk.file.md5 == null) {
                    fileMd5(chunk.file.file, function (err, md5) {
                        chunk.file.uniqueIdentifier = md5;
                        chunk.preprocessFinished();
                    });
                } else {
                    chunk.preprocessFinished();
                }
            },
            //递归格式化数据
            convert(childNodes) {
                for (var i = 0; i < childNodes.length; i++) {
                    childNodes[i].label = childNodes[i].text;
                    childNodes[i].title = childNodes[i].text;
                    if (childNodes[i].children) {
                        this.convert(childNodes[i].children);
                    }
                }
            },
            //目录树名称过长处理
            renderContent(h, {node, data, store}) {
                var text = node.label;
                if (text && text.length > 13) {
                    text = text.substring(0, 13) + "...";
                }
                return (
                    < el-tooltip
                placement = "right-start" >
                    < div
                slot = "content" > {node.label
                    }<
                    /div>
                    < span
                style = "font-size: 12px" > {text} < /span>
                    < /el-tooltip>
            )
                ;
            },
            //目录树右键菜单
            handleContextMenu(e, data, node, o) {
                e.preventDefault();
                e.stopPropagation();
                this.contextNode = data;
                var x = e.clientX + 2;
                var y = e.clientY + 2;
                // Get the current location
                this.contextMenuData.axios = {
                    x,
                    y
                };
            },
            //分类管理
            handleNodeClick(data) {
                this.current = 1;
                this.tDirectoryId = data.id;
                this.tDirectoryText = data.text;
            },
            //获取目录树
            getTreeDate() {
                var treeUrl = "mvc/getDirTree";
                this.$axios.post(treeUrl, {}).then(res => {
                    this.treeData = res.data;
                    this.convert(this.treeData);
                });
            },

            // //拓扑图预览
            // handlePreview() {
            //
            //     this.modalPreviewFile = true
            //     this.$nextTick(() => {
            //         this.renderTopology()
            //         this.loadJSONData(this.jsonStr)
            //     })
            //
            // },
            //文件上传
            fileUpload() {
                if (this.tDirectoryId == 0 || this.tDirectoryText == "") {
                    this.$Modal.info({
                        title: "提示",
                        content: "请选则一个目录！"
                    });
                } else {
                    this.isFileUp = true;
                }
            },
            //获取页面table数据
            getAllData() {
                let _self = this;
                this.$axios.post("mvc/fileParser/getList", {}).then(async (res) => {
                    this.initHandleOk();
                    this.parsers = res.data;
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "预分类",
                            fatherClassName: "1",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.preClassDataFor1 = res.data.list;
                            for (var i = 0; i < this.preClassDataFor1.length; i++) {
                                this.preClassDataFor1[i]._checked = true;
                            }
                            this.handlePreSelectionChange1(this.preClassDataFor1);
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "预分类",
                            fatherClassName: "2",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.preClassDataFor2 = res.data.list;
                            for (var i = 0; i < this.preClassDataFor2.length; i++) {
                                this.preClassDataFor2[i]._checked = true;
                            }
                            this.handlePreSelectionChange2(this.preClassDataFor2);
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "预分类",
                            fatherClassName: "3",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.preClassDataFor3 = res.data.list;
                            for (var i = 0; i < this.preClassDataFor3.length; i++) {
                                this.preClassDataFor3[i]._checked = true;
                            }
                            this.handlePreSelectionChange3(this.preClassDataFor3);
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "预分类",
                            fatherClassName: "4",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.preClassDataFor4 = res.data.list;
                            for (var i = 0; i < this.preClassDataFor4.length; i++) {
                                this.preClassDataFor4[i]._checked = true;
                            }
                            this.handlePreSelectionChange4(this.preClassDataFor4);
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "预分类",
                            fatherClassName: "5",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.preClassDataFor5 = res.data.list;
                            for (var i = 0; i < this.preClassDataFor5.length; i++) {
                                this.preClassDataFor5[i]._checked = true;
                            }
                            this.handlePreSelectionChange5(this.preClassDataFor5);
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "预分类",
                            fatherClassName: "6",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.preClassDataFor6 = res.data.list;
                            for (var i = 0; i < this.preClassDataFor6.length; i++) {
                                this.preClassDataFor6[i]._checked = true;
                            }
                            this.handlePreSelectionChange6(this.preClassDataFor6);
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "预分类",
                            fatherClassName: "7",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.preClassDataFor7 = res.data.list;
                            for (var i = 0; i < this.preClassDataFor7.length; i++) {
                                this.preClassDataFor7[i]._checked = true;
                            }
                            console.log(this.preClassDataFor7)
                            this.handlePreSelectionChange7(this.preClassDataFor7);
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "待分类",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.waitClassData = res.data.list;
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/pageFilesByIsParser", {
                            classType: "其他",
                            isParser: 0,
                            isExport: 0,
                            page: 1,
                            limit: _self.limit
                        })
                        .then(res => {
                            this.otherData = res.data.list;
                            this.initHandleOk();
                        });
                    this.$axios
                        .post("mvc/fileParser/getList", {})
                        .then(res => {
                            this.parsers = res.data;
                            this.initHandleOk();
                        });

                });
                //已解析数据清空
                this.afterPreClassData = [];
                this.afterWaitClassData = [];
                this.afterOtherData = [];
                //解析进度初始化
                this.parsed = 0;
            },
            initHandleOk(currentType, data=[]) {
                this.initHandleOkState++;
                if(this.initHandleOkState >= 12 && data.length>0){
                    console.log( this.preClassColumnsParams, 'initHandleOkState')
                    let params = data;
                    for(let i = 0,len = params.length; i< len; i++) {
                        this.currentIndex = i;
                        this.currentParser = params[i].recommendParserId;
                        this.currentType = currentType;
                        this.handleOk()
                    }

                }
            },
            //执行多文件解析
            handleMultiParse() {

                this.parsed = 0;
                this.allParse =
                    this.preSelection1.length +
                    this.preSelection2.length +
                    this.preSelection3.length +
                    this.preSelection4.length +
                    this.preSelection5.length +
                    this.preSelection6.length +
                    this.preSelection7.length +
                    this.waitSelection.length +
                    this.otherSelection.length;
                if (this.allParse === 0) {
                    this.$notify({
                        title: "提示",
                        message: "请选择解析的数据",
                        type: "error"
                    });
                    return;
                }
                if (this.preSelection1.length > 0) {
                    this.afterPreClassData = [];
                    this.preSelection1.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.preSelection1[index]])
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.preSelection1[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterPreClassData = this.afterPreClassData.concat(
                                        res.data
                                    );
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.preSelection1[index].name + "解析失败",
                                    type: "error"
                                });
                            });
                    });
                }
                if (this.preSelection2.length > 0) {
                    this.afterPreClassData = [];
                    this.preSelection2.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.preSelection2[index]])
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.preSelection2[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterPreClassData = this.afterPreClassData.concat(
                                        res.data
                                    );
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.preSelection2[index].name + "解析失败",
                                    type: "error"
                                });
                            });
                    });
                }
                if (this.preSelection3.length > 0) {
                    this.afterPreClassData = [];
                    this.preSelection3.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.preSelection3[index]])
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.preSelection3[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterPreClassData = this.afterPreClassData.concat(
                                        res.data
                                    );
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.preSelection3[index].name + "解析失败",
                                    type: "error"
                                });
                            });
                    });
                }
                if (this.preSelection4.length > 0) {
                    this.afterPreClassData = [];
                    this.preSelection4.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.preSelection4[index]])
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.preSelection4[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterPreClassData = this.afterPreClassData.concat(
                                        res.data
                                    );
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.preSelection4[index].name + "解析失败",
                                    type: "error"
                                });
                            });
                    });
                }
                if (this.preSelection5.length > 0) {
                    this.afterPreClassData = [];
                    this.preSelection5.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.preSelection5[index]])
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.preSelection5[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterPreClassData = this.afterPreClassData.concat(
                                        res.data
                                    );
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.preSelection5[index].name + "解析失败",
                                    type: "error"
                                });
                            });
                    });
                }
                if (this.preSelection6.length > 0) {
                    this.afterPreClassData = [];
                    this.preSelection6.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.preSelection6[index]])
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.preSelection6[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterPreClassData = this.afterPreClassData.concat(
                                        res.data
                                    );
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.preSelection6[index].name + "解析失败",
                                    type: "error"
                                });
                            });
                    });
                }
                if (this.preSelection7.length > 0) {
                    this.afterPreClassData = [];
                    this.preSelection7.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.preSelection7[index]])
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.preSelection7[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterPreClassData = this.afterPreClassData.concat(
                                        res.data
                                    );
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.preSelection7[index].name + "解析失败",
                                    type: "error"
                                });
                            });
                    });
                }
                if (this.waitSelection.length > 0) {
                    this.afterWaitClassData = [];
                    this.waitSelection.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.waitSelection[index]])
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.waitSelection[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterWaitClassData = this.afterWaitClassData.concat(
                                        res.data
                                    );
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.waitSelection[index].name + "解析失败",
                                    type: "error"
                                });
                            });
                    });
                }
                if (this.otherSelection.length > 0) {
                    this.afterOtherData = [];
                    this.otherSelection.forEach((item, index) => {
                        this.$axios
                            .post("mvc/fileParser/multiParse", {
                                selection: JSON.stringify([this.otherSelection[index]]),
                                user: localStorage.getItem("ms_username")
                            })
                            .then(res => {
                                if (res.data.success === false) {
                                    this.$notify({
                                        title: "提示",
                                        message:
                                            "文件" +
                                            this.otherSelection[index].name +
                                            "解析失败:" +
                                            res.data.data,
                                        type: "error"
                                    });
                                } else {
                                    this.afterOtherData = this.afterOtherData.concat(res.data);
                                    this.parsed++;
                                }
                            })
                            .catch(e => {
                                this.$notify({
                                    title: "提示",
                                    message: "文件" + this.otherSelection[index].name + "解析失败",
                                    type: "error"
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
                this.waitSelection = selection;
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
                this.afterWaitSelection = selection;
            },
            //解析后其他分类选中
            handleAfterOtherSelectionChange(selection) {
                this.afterOtherSelection = selection;
            },
            //解析器选择确认
            handleOk() {
                this.fixCon = false;
                let fileIdList = [];
                let current = this.parsers.find(i => i.id == this.currentParser);

                if(!current){
                    return;
                }
                if (this.currentType == "预分类1") {
                    if(this.preClassDataFor1.length>0 && this.preClassDataFor1[this.currentIndex]) {
                        this.preClassDataFor1[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.preClassDataFor1[this.currentIndex].id);
                        this.preClassDataFor1[this.currentIndex].recommendParserName =
                            current.name;
                    }

                } else if (this.currentType == "预分类2") {
                    if(this.preClassDataFor2.length>0&& this.preClassDataFor2[this.currentIndex]) {
                        this.preClassDataFor2[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.preClassDataFor2[this.currentIndex].id);
                        this.preClassDataFor2[this.currentIndex].recommendParserName =
                            current.name;
                    }

                } else if (this.currentType == "预分类3") {
                    if(this.preClassDataFor3.length>0&& this.preClassDataFor3[this.currentIndex]) {
                        this.preClassDataFor3[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.preClassDataFor3[this.currentIndex].id);
                        this.preClassDataFor3[this.currentIndex].recommendParserName =
                            current.name;
                    }

                } else if (this.currentType == "预分类4") {
                    if(this.preClassDataFor4.length>0&& this.preClassDataFor4[this.currentIndex]) {
                        this.preClassDataFor4[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.preClassDataFor4[this.currentIndex].id);
                        this.preClassDataFor4[this.currentIndex].recommendParserName =
                            current.name;
                    }

                } else if (this.currentType == "预分类5") {
                    if(this.preClassDataFor5.length>0&& this.preClassDataFor5[this.currentIndex]) {
                        this.preClassDataFor5[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.preClassDataFor5[this.currentIndex].id);
                        this.preClassDataFor5[this.currentIndex].recommendParserName =
                            current.name;
                    }

                } else if (this.currentType == "预分类6") {
                    if(this.preClassDataFor6.length>0&& this.preClassDataFor6[this.currentIndex]) {
                        this.preClassDataFor6[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.preClassDataFor6[this.currentIndex].id);
                        this.preClassDataFor6[this.currentIndex].recommendParserName =
                            current.name;
                    }

                } else if (this.currentType == "预分类7") {
                    console.log(this.preClassDataFor7[this.currentIndex])
                    if(this.preClassDataFor7.length>0&& this.preClassDataFor7[this.currentIndex]){
                        this.preClassDataFor7[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.preClassDataFor7[this.currentIndex].id);
                        this.preClassDataFor7[this.currentIndex].recommendParserName =
                            current.name;
                    }

                } else if (this.currentType == "待分类") {
                    if(this.waitClassData.length>0&& this.waitClassData[this.currentIndex]) {
                        this.waitClassData[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.waitClassData[this.currentIndex].id);
                        this.waitClassData[this.currentIndex].recommendParserName =
                            current.name;
                    }

                } else {
                    if(this.otherData.length>0&& this.otherData[this.currentIndex]) {
                        this.otherData[this.currentIndex].recommendParserId = current.id;
                        fileIdList.push( this.otherData[this.currentIndex].id);
                        this.otherData[this.currentIndex].recommendParserName = current.name;
                    }
                }
                const id = current.id;
                this.$axios
                    .post("mvc/fileParser/getParamList", {
                        parserId: id
                    })
                    .then(res => {
                        this.$set(this.idPropertiesMap, id, res.data);


                        for(let i=0;i<fileIdList.length;i++){
                            this.$set(this.fileCountMap,fileIdList[i], res.data);
                        }

                        this.idPropertiesMapTemp = Object.assign({},this.idPropertiesMap,this.idPropertiesMapTemp);

                    });
            },
            handleSetOk() {
                console.dir(this)
                let propertiesMap = {};
                //接着改
                // console.log(this.fileCountMap) //文件详情
                // console.log(this.idPropertiesMap)
                console.log(this.currentFileId)//文件ID
                // console.log(this.currentParser)//解析器ID
                //     let newparsid=Date.now().toString();
                //     console.log(newparsid);

                    // this.$set(this.fileCountMap, this.currentFileId, this.idPropertiesMap[this.currentParser]);

                    // this.$set(this.idPropertiesMap, newparsid, this.idPropertiesMap[this.currentParser]);
                    // this.$set(this.idPropertiesMap, this.currentParser, this.idPropertiesMapTemp[this.currentParser]);
                    // this.idPropertiesMapTemp = Object.assign({},this.idPropertiesMap,this.idPropertiesMapTemp);
                    // console.dir(this.idPropertiesMapTemp);
                    // this.currentParser=newparsid;
                    // console.dir((this.parsers));
                    // if (this.currentType == "预分类2") {
                    //
                    //     this.preClassDataFor2[this.currentIndex].recommendParserId = newparsid;
                    //
                    // }else if(this.currentType == "待分类"){
                    // }else {
                    // }


                propertiesMap.paramList = this.idPropertiesMap[this.currentParser];

                console.log(this.idPropertiesMap)
                console.log(this.currentParser)

            },
            //****多文件解析添加映射******

            cancel11 () {

                this.$Message.info('点击了取消');
                this.$set(this.customKeysObj, this.tempFileId, this.selectData);
                this.$set(this.columnKeyNamesMapObj, this.tempFileId, this.columnKeyNamesMap);
                // alert(this.customKeysObj);
                // this.customKeysObj.set(this.tempFileId, this.selectData);
                // this.columnKeyNamesMapObj.set(this.tempFileId,this.columnKeyNamesMap);
            },
            //获取当前点击预览的文件所有的key（也就是列名）
            getAllKeys(obj){
                // alert(obj);
                let firstJson;
                Object.keys(obj).forEach((key, index)=>{
                    let json=obj[key][0];
                    firstJson=json;
                });
                let keyStr=[];
                Object.keys(firstJson).forEach((key, index)=>{
                    keyStr.push(key)
                });
                return keyStr;
            },
            getAllKeysAll(obj){
                // alert(obj);
                let firstJson;
                let keyStr=[];
                Object.keys(obj).forEach((key, index)=>{
                    let json=obj[key][index];
                    firstJson=json;
                    let keyStrSon=[];
                    Object.keys(firstJson).forEach((key, index)=>{
                        keyStrSon.push(key);
                    });
                    keyStr.push(keyStrSon);
                });


                return keyStr;
            },

            getDicColumnsByDicName(dicTable, key) {
                this.$axios.post('mvc/getDicColumnsByDicName', {
                    dicName: dicTable
                }).then(res => {
                    this.$set(this.columnSelectMap[key], 'dicColumns', res.data)

                });
            },

            handleRightRowClick(row) {
                console.log(row);
                this.highLightRow(row);
                this.$refs.result.innerHTML =
                    '<textarea id="ID"  style="width:100%;min-height:600px;overflow:scroll;resize:none;" >' +
                    row.parseResult +
                    "</textarea>";
                this.allKey=this.getAllKeys(JSON.parse(row.parseResult.toLowerCase()));
                this.allKeyAll=this.getAllKeysAll(JSON.parse(row.parseResult.toLowerCase()));
                this.tempFileId=row.id;
                if(this.columnKeyNamesMapObj.hasOwnProperty(row.id)){
                    delete this.columnKeyNamesMapObj[row.id];
                }
                this.columnKeyNamesMap={};

                this.jsonTables = JSON.parse(row.parseResult.toLowerCase());

                //解析匹配到得表名
                // this.table_name = this.jsonTables.table_name;
                this.table_name = "fms_file";

                this.getColumns(this.table_name);
                this.changeDefaultTab()
                 this.genParamsByAllKey();

            },

            changeDefaultTab() {
                const defaultKey = Object.keys(this.jsonTables)[0]
                this.toggleTab(0, defaultKey)
            },

            async genParamsByAllKey() {
                // 获取库
                await this.getSchemas()

                if(this.allKey != null){
                    this.allKey.forEach((key,index) => {
                        let columnValue = {
                            'schemaId': '',
                            'tableId': '',
                            'columnId': ''
                        }

                        this.$set(this.columnKeyNamesMap, key, columnValue)

                        let columnSelectValue = {
                            'schemas': this.schemas,
                            'tables': '',
                            'columns': ''
                        }
                        this.$set(this.columnSelectMap, key, columnSelectValue)
                    })

                    // 获取模板下拉框
                    this.getTemplateList(this.allKey);
                }
            },
            // 获取模板下拉框
            getTemplateList(allKey) {

                let me = this;
                $.ajax({
                    url:"mvc/getColumnMapRelation",
                    type:"GET",
                    traditional: true,
                    data: {
                        "columnKeys": allKey,
                    },
                    success: function(res) {
                        // 匹配度最高的模板
                        me.templateNameInfos = res.templateNameInfos;

                    }
                })
            },

            // 模板名改变时触发的函数
            onChangeTemplate(templateName,allkey) {
                let me = this;
                $.ajax({
                    url:encodeURI(encodeURI("mvc/getColumnMapRelationByTemplateName")),
                    type:"GET",
                    traditional: true,
                    data: {
                        columnKeys: this.allKey,
                        templateName: templateName
                    },
                    success: (res) => {
                        if(res.columnMapRelations!=null){
                            me.columnMapRelations = res.columnMapRelations;
                            var map = {};
                            me.columnMapRelations.forEach(function(ele) {

                                map[ele.columnKey] = ele;

                            })
                            me.columnKeyNamesMap = map;
                            this.allKey.forEach((key,index) => {
                                let columnValue = {
                                    'schemaId': '',
                                    'tableId': '',
                                    'columnId': ''
                                }
                                if(!map[key]) {
                                    this.$set(this.columnKeyNamesMap, key, columnValue)
                                }

                            })

                            for(let key in this.columnData) {

                                this.getTables(this.columnKeyNamesMap[key].schemaId, key)
                                this.getColumnsByTable(this.columnKeyNamesMap[key].tableId, key)
                                this.getDicByColumn(this.columnKeyNamesMap[key].columnId, key)
                            }

                        }
                    }
                })
            },
            // 获取库
            getSchemas() {
                return this.$axios.post('mvc/getAllSchemas').then(res => {
                    this.schemas = res.data
                })
            },

            getTables(schemaId, key) {
                if(!schemaId && schemaId == '') {return;}
                this.$axios.post('mvc/getTablesBySchemaId', {
                    schemaId: schemaId
                }).then(res => {
                    this.$set(this.columnSelectMap[key], 'tables', res.data)
                })
            },
            getColumnsByTable(tableId, key) {
                if(!tableId && tableId == '') {return;}
                this.$axios.post('mvc/getColumnsForTable', {
                    tableId
                }).then(res => {
                    const data = res.data || []
                    const dicData = data.filter(v => v.isDic === 0)
                    this.$set(this.columnSelectMap[key], 'columns',dicData)

                })
                this.getDicByTableId(tableId, key)
            },
            getDicByColumn(columnId, key) {
                //选择字段下拉框选择后，暂时不需要处理
                this.$set(this.columnKeyNamesMap[key], 'columnId', columnId);
            },
            getDicByTableId(tableId, key) {
                this.$axios.post('mvc/getDicNameByTableId', {
                    tableId
                }).then(res => {
                    const dicTables = res.data || []
                    this.$set(this.columnSelectMap[key], 'dicTables', dicTables)
                    this.$set(this.columnKeyNamesMap[key], 'dicMap', {})

                    dicTables.forEach(dicTable => {
                        const { columnEnglish } = dicTable
                        this.$set(this.columnKeyNamesMap[key]['dicMap'], dicTable.columnEnglish, '')
                    })

                })
            },
            toggleTab(index,key) {
                this.tableIndex = index;
                this.tableKey = key
            },

            handleUploadFile(file) {
                this.uploadListFile.push(file);
                file.status = 'finished'
                this.$refs.uploadFile[0].fileList.push(file);
                return false;

            },
            uploadSuccessFile(response, file, fileList) {

                this.resultFils = file.response;

                this.loadingStatus = false;
                this.$Message.success('This is a success tip');
            },
            handleRemoveFile(file, fileList) {
                let idx = this.uploadListFile.findIndex((f) => {
                    return f.name == file.name;
                })
                this.uploadListFile.splice(idx, 1);
            },

            uploadErrorFile() {
                this.loadingStatus = false;
                this.$Message.error('上传文件失败');
            },


            FileUpload() {

                let length = this.uploadListFile.length;
                if (length != 1) {
                    this.$Message.error('有且只能上传一个文件');
                    return false;
                }
                this.loadingStatus = true;
                let FileNameList = [];
                this.$refs.uploadFile[0].clearFiles();
                if (this.uploadListFile) {
                    this.uploadListFile.forEach(file => {
                        this.$refs.uploadFile[0].post(file);
                    })
                }
            },

            //计算表格输入框的最大长度
            tableInputMaxLength(key) {
                key = this.selectData[key];
                for (let i = 0; i < this.allTableField.length; i++) {
                    let field = this.allTableField[i];
                    if (field.column_name == key && field.max_length != undefined) {
                        return field.max_length;
                    }
                }
                return;
            },

            getColumns(table_name) {
                var me = this;
                this.$axios.post('mvc/listColumns', {
                    tableName: table_name
                }).then(res => {
                    if (res.data) {
                        me.allTableField = res.data;
                        let columnData = {};
                        me.allKey.forEach(key => {

                            let keyData = [];
                            for (let index in res.data) {
                                let column_name = res.data[index].column_name;
                                let isPri = res.data[index].column_key == 'PRI' ? true : false
                                if (key == column_name && !isPri) {
                                    keyData = [column_name];
                                    break;
                                } else if (!isPri) {
                                    keyData.push(column_name);
                                }
                            }
                            columnData[key] = keyData;
                        });

                        let selectData = {};

                        for (let key in columnData) {
                            if (columnData[key].length > 0) {
                                selectData[key] = columnData[key][0];
                            }
                        }
                        me.selectData = selectData;
                        me.columnData = columnData;

                    }
                })
            },

            // 保存到新模板
            saveTemplate(l) {
                let arr = []

                for (let key in this.columnKeyNamesMap) {
                    let obj = {};
                    obj["columnKey"] = key;
                    obj["schemaId"] = this.columnKeyNamesMap[key].schemaId;
                    obj["tableId"] = this.columnKeyNamesMap[key].tableId;
                    obj["columnId"] = this.columnKeyNamesMap[key].columnId;
                    if(this.columnKeyNamesMap[key]["columnId+''"]!=undefined){
                        obj["columnId"] = this.columnKeyNamesMap[key]["columnId+''"];
                    }

                    obj["dicMap"] = this.columnKeyNamesMap[key]['dicMap'];
                    if (l == 1) {
                        if(this.selectTemplateName==null || this.selectTemplateName==undefined || this.selectTemplateName=="" ){
                            this.$message.warning("请填写模板名称");
                            return false;
                        }else{
                            obj["templateName"] = this.selectTemplateName;
                        }

                    } else {
                        obj["templateName"] = this.newTemplateName;
                    }
                    arr.push(obj);
                }

                this.$axios.post('mvc/addColumnMapRelations', {
                    formList:JSON.stringify(arr),

                }).then(res => {
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                })
            },
            // 保存映射关系到新模板   TODO 显示前判断
            saveTemplateToNew(){
                this.templateNameVisible = true;
            },

            //左列table rowclick 事件1
            handleLeftRowClick(row) {
                // this.highLightRow(row);
                // let fileServerPath = this.config.fileServerPath;
                // let previewPath = this.config.previewPath;
                // let fileUrl = fileServerPath + "/" + row.groups + "/" + row.realPath;
                // let uri = previewPath + encodeURIComponent(fileUrl);
                // this.$refs.result.innerHTML =
                //     '<iframe src="' + uri + '" height="600" width="98%"></iframe>';

                this.preview(row);
            },
            highLightRow(row) {
                this.viewFileName = row.name;
                this.fixCon = true;
                if (!(row.classType == "预分类" && row.isParser == 0)) {
                    this.$refs.currentRowTable1.clearCurrentRow();
                }
                if (!(row.classType == "待分类" && row.isParser == 0)) {
                    this.$refs.currentRowTable2.clearCurrentRow();
                }
                if (!(row.classType == "其他" && row.isParser == 0)) {
                    this.$refs.currentRowTable3.clearCurrentRow();
                }
                if (!(row.classType == "预分类" && row.isParser == 1)) {
                    this.$refs.currentRowTable4.clearCurrentRow();
                }
                if (!(row.classType == "待分类" && row.isParser == 1)) {
                    this.$refs.currentRowTable5.clearCurrentRow();
                }
                if (!(row.classType == "其他" && row.isParser == 1)) {
                    this.$refs.currentRowTable6.clearCurrentRow();
                }
            },
            handleExport() {
                this.$message('执行导入中');
                if (this.parsed == 0) {
                    this.$notify({
                        title: "提示",
                        message: "请先解析数据再导入",
                        type: "error"
                    });
                    return;
                }
                let data = [];
                let parserData = [];
                let dataId=[];
                //加入预分类解析数据
                //这句代码以前是最外层循环，应该是写错了 会导致数据多次重复
                // this.afterPreClassData.forEach(item => {
                    this.afterPreSelection.forEach(item => {
                    if ((item!=null)) {
                            data.push(item.parseResult);//把每个文件的id拼接在数据之后
                            parserData.push({
                                parserId: item.recommendParserId,
                                fileId: item.id
                            });

                            //目的是去重
                            //[{"fileId":1551093019920,"parserId": 1551143976882},{"fileId": 1551093017005,"parserId": 1551143976882}]
                            //分隔符
                        //     let object = {};
                        //     let data2 = [];
                        //     if(parserData.length>1){
                        //     for(let j=0;j<parserData.length;j++){
                        //         if(object != {}){
                        //         if(parserData[j].fileId == object.fileId && parserData[j].parserId == object.parserId){
                        //             data2.push ({
                        //                 parserId: parserData[j].fileId,
                        //                 fileId: parserData[j].parserId
                        //                 });
                        //             };
                        //         };
                        //         object= parserData[j];
                        //     };
                        //     parserData = data2;
                        // };
                        //     //这里也是为了给dataId去重
                        //     if(dataId != null && dataId.length != 0){
                        //         for(let j=0;j<dataId.length;j++){
                        //             if(dataId[j]==item.id){
                        //                 break;
                        //             }
                        //         };
                        //     }else {
                        //         dataId.push(item1.id);//加入id标识
                         dataId.push(item.id);//加入id标识
                            // };
                            //分隔符
                        };
                    });
                // });
                //加入待分类解析数据

                this.afterWaitSelection.forEach(item => {
                    if ((item!=null)) {
                        data.push(item.parseResult);//把每个文件的id拼接在数据之后
                        parserData.push({
                            parserId: item.recommendParserId,
                            fileId: item.id
                        });
                                dataId.push(item.id);//加入id标识
                            //分隔符
                    };
                });
                //加入未分类解析数据

                this.afterOtherSelection.forEach(item => {
                    if ((item!=null)) {
                        data.push(item.parseResult);//把每个文件的id拼接在数据之后
                        parserData.push({
                            parserId: item.recommendParserId,
                            fileId: item.id
                        });
                        dataId.push(item.id);//加入id标识
                        //分隔符
                    };
                });
                if (data.length == 0) {
                    this.$notify({
                        title: "提示",
                        message: "请选择入库数据",
                        type: "error"
                    });
                    return;
                }


                this.$axios
                    .post("mvc/fileParser/multiParseSaveDataToHBase", {
                        dataJSON: data.join("#,##,#-#-#,##,#"),
                        parserDataJSON: JSON.stringify(parserData),
                        dataIdJSON:dataId.join("#,##,#-#-#,##,#"),
                        customKeysObj:JSON.stringify(this.customKeysObj),
                        columnKeyNamesMapObj:JSON.stringify(this.columnKeyNamesMapObj)
                    })
                    .then(res => {
                        this.$notify({
                            title: "提示",
                            message: res.data.data,
                            type: res.data.success ? "success" : "error"
                        });
                        if (res.data.success) {
                            this.getAllData();
                        }
                    })
                    .catch(e => {
                        this.$notify({
                            title: "提示",
                            message: e.response.data.errorText,
                            type: "error"
                        });
                    });
            },

            // 新版预览
            preview(row){
                // // 新版预览
                // let previewPath = this.config.previewPath;
                //
                // var previewSrc ="http://"+ previewPath +"/index?filePath="+this.selectFileList[0].realPath+'&id='+this.selectFileList[0].id;
                // var ifr = document.createElement('iframe');
                // ifr.src = previewSrc;
                // document.body.appendChild(ifr);
                // 新版预览
                let previewPath = this.config.previewPath;

                // var previewSrc ="http://"+ previewPath +"/index?filePath="+row.selectFileList[0].realPath+'&id='+row.selectFileList[0].id;
                var previewSrc ="http://"+ previewPath +"/index?filePath="+row.realPath+'&id='+row.id;
                var ifr = document.createElement('iframe');
                ifr.src = previewSrc;
                document.body.appendChild(ifr);




            }
        },
        watch: {
            numOfSelect(val, oldval) {
                this.limit = val;
                this.getAllData();
            },
            idPropertiesMap:{//深度监听，可监听到对象、数组的变化
                handler(val, oldval){
                    console.log(val)
                    console.log(oldval)

                },
                deep:true
            }
        }


    };

</script>

<style scoped>
    .drag-box {
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
    .tableActive a{
        color:#666;
    }
    .tableActive {
        border: 1px solid #409eff;
        background-color: #409eff;
    }
    .title-fixed {
        position: fixed;
        top: 70px;
        z-index: 99;
        background-color: rgb(247, 247, 247);
        width: 570px;
    }
    .item-title {
        padding: 8px 8px 8px 12px;
        font-size: 14px;
        line-height: 1.5;
        color: #24292e;
        font-weight: 600;
    }
    .demo-spin-icon-load {
        animation: ani-demo-spin 1s linear infinite;
    }
    .ivu-spin-fix {
        top: 30px;
    }
    .item-title-sel {
        padding: 8px 8px;
        font-size: 14px;
        line-height: 1.5;
        color: #24292e;
        font-weight: 200;
    }
</style>
