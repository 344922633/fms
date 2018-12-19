<style scoped>
    .tableActive a{
        color:#666;
    }
    .tableActive {
        border: 1px solid #409eff;
        background-color: #409eff;
    }

    .tabClass{
        float: left;
        margin: 3px 5px 2px 3px;
        border-radius: 3px;
        font-size: 12px;
        overflow: hidden;
        cursor: pointer;
        height: 23px;
        line-height: 23px;
        border: 1px solid #e9eaec;
        padding: 0 5px 0 12px;
        vertical-align: middle;
        color: #666;
        transition: all .3s ease-in;
    }

    #editor {
        min-height: 500px;
    }
</style>
<template>
    <div>
        <Form ref="formCustom" :label-width="80">
            <Row>
                <i-col span="4">
                    <div v-if="file">文件名：{{ file.name }}</div>
                </i-col>
                <i-col span="6">
                    <FormItem label="解析器">
                        <Select filterable v-if="file" v-model="file.recommendParserId" @on-change="getparamList">
                            <Option v-for="item in parserData" :value="item.id" :key="item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>
                </i-col>
                <i-col span="6" prop="parserExtList"  v-for="item in parserExtList">
                    <FormItem :label="item.parameterDesc">
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
                </i-col>
                <i-col span="4">
                    <Button @click="handleParse" :loading="loading">解析</Button>
                </i-col>
                <i-col span="4">
                    <Button @click="downloadJsonFile">导出</Button>
                </i-col>
                <i-col span="4">
                    <Button @click="handlePreview">预览拓扑图</Button>
                </i-col>
            </Row>
            <Divider>json</Divider>
            <div ref="result"></div>
      <!--      <Divider>预览</Divider>
            <iframe v-if="previewFileData" :src="previewFileData" height="600px" width="1200px"></iframe>
            <div ref="tuopu" height="300px" align="center">
                <el-button type="text" icon="el-icon-edit" @click="handlePreview">预览拓扑图</el-button>
            </div>-->
            <!--拓扑图预览弹出窗口-->
            <Modal
                title="预览拓扑图"
                width="1500px"
                scrollable
                v-model="modalPreviewFile"
                :mask-closable="false">
                <!--<div class="layout">-->
                    <div id="editor" data-options="region:'center'"></div>
                <!--</div>-->
                <div slot="footer">
                    <Input style="width: 150px" v-model="topologyName" placeholder="请输入拓扑图名称" />
                    <Button @click="submitTopology" type="primary">保存</Button>
                    <Button @click="modalPreviewFile = false">取消</Button>
                </div>
            </Modal>

            <div ref="parsejson"></div>

            <div class="tabs" style="height:39px">
               <div style="float: left;margin-right: 10px; width: 100px;float: left; margin: 3px 5px 2px 3px;border-radius: 3px;font-size: 12px;overflow: hidden;cursor: pointer;height: 23px;line-height: 23px;border: 1px solid #e9eaec;background: #fff;padding: 0 5px 0 12px;vertical-align: middle;color: #666;transition: all .3s ease-in;"
               :class="{tableActive:index == tableIndex}" v-for="(value,key,index) in jsonTables" @click="toggleTab(index, key)"><a>{{key}}</a></div>
           </div>
            <!--改表格高度-->
         <div v-if="jsonTables && Object.keys(jsonTables).length" style="height: 300px; overflow-y: auto;">
           <table style="border-right:1px solid #ddd;border-bottom:1px solid #ddd;width:100%;overflow-x:scroll;" v-for="(value,key,index) in jsonTables" :key="key" :label="value" :value="key" v-show="index == tableIndex" border="0" cellspacing="0" cellpadding="0">
                   <thead>
                   <tr>
                       <th style="background: #c0c4cc;border: 1px solid #ddd;" v-for="keyvalue in tableKeysForDisplay">
                            {{keyvalue}}
                       </th>
                   </tr>
                   </thead>
                   <tbody>
                   <tr v-for="(value, key) in [...value]">

                       <td v-for="keyvalue in tableKeysForDisplay" style="text-align: center;border-left:1px solid #ddd;border-top:1px solid #ddd">{{value[keyvalue]}}</td>
                   </tr>
               </tbody>
              </table>
         </div>



           <!-- <Table :columns="tableShowColumns" :data="tableShowData" style="width:100%;overflow-x:scroll;"
                   @on-row-dblclick="tableRowDoubleClick"></Table> -->
            <Divider> 解析字段</Divider>
            <div v-if="columnData && Object.keys(columnData).length" style="height: 300px; overflow-y: auto;">
            <Form inline v-for="(data,key) in columnData" :key="key">
                <FormItem :label-width="100" :label="key">
                </FormItem>
                <FormItem label="选择库">
                    <Select
                        @on-change="(schemaId) => getTables(schemaId, key)"
                        style="width: 180px"
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
                        <Option v-for="(table,tableIdx) in columnSelectMap[key].tables" :value="table.id" :key="table.id"> {{ table.tableChinese }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="选择字段">
                    <Select
                        @on-change="(columnId) => getDicByColumn(columnId, key)"
                        style="width: 180px"
                        v-model="columnKeyNamesMap[key].columnId"
                        :clearable="true"
                    >
                        <Option v-for="(column,columnIdx) in columnSelectMap[key].columns" :value="column.id" :key="column.id"> {{ column.columnChinese }}</Option>
                    </Select>
                </FormItem>
                <template v-if="columnSelectMap[key] && columnSelectMap[key].dicTables">
                    <FormItem v-for="dicTable in columnSelectMap[key].dicTables" :label="dicTable.columnChinese">
                        <Select
                            style="width: 180px"
                            v-model="columnKeyNamesMap[key]['dicMap'][dicTable.columnEnglish]"
                            :clearable="true"
                        >
                            <Option v-for="(dic,dicIdx) in dicTable.dicList" :value="dic.DM" :key="dic.DM"> {{ dic.MC }}</Option>
                        </Select>
                    </FormItem>
                </template>
            </Form>
            </div>
            <!--<Row>
                <i-col span="8">
                    <FormItem label="Password" prop="passwd">
                        <Input type="password" v-model="formCustom.passwd"></Input>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="Confirm" prop="passwdCheck">
                        <Input type="password" v-model="formCustom.passwdCheck"></Input>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="Age" prop="age">
                        <Input type="text" v-model="formCustom.age" number></Input>
                    </FormItem>
                </i-col>
            </Row>-->
        </Form>
<!--
        <Divider> 解析数据</Divider>
-->
        <div ref="table" v-show="false"></div>
        <Button type="primary" @click="parseDataSaveDatabase">入库</Button>
        <Button type="primary" @click="handleSaveMapInfo">保存映射关系</Button>

        <!--<Modal-->
            <!--title="修改数据"-->
            <!--v-model="updateTableRowDataIsShow"-->
            <!--@on-ok="updateTableRowData"-->
            <!--:mask-closable="false">-->
            <!--<Form label-position="right" :label-width="120">-->
                <!--<FormItem :label="item.key" v-for="(item,index) in tableShowColumns" :key="index">-->
                    <!--<Input v-model="currentRowData[item.key]" style="width: 300px"-->
                           <!--:maxlength="tableInputMaxLength(item.key)"/>-->
                <!--</FormItem>-->
            <!--</Form>-->
        <!--</Modal>-->
    </div>
</template>
<script>
    import ICol from "../../../../node_modules/iview/src/components/grid/col.vue";
    import Vue from 'vue';
    import Bus from '@/components/common/bus'

    const originData = {
        modalPreviewFile: false,//预览弹出窗口
        previewFileData: '',
        uploadListFile: [],
        loadingStatus: false,
        fileList: [],
        resultFils: "",
        loading: false,
        //解析结果字段
        fields: [],
        //解析结果json字符
        jsonStr: '',
        config: {},
        previewSrc: '',
        parseStr: '',
        //解析结果列表
        tableData: [],
        columnData: {},
        selectData: {},
        table_name: '',
        tableName: '',
        tableNames: [],
        tableColumns: [],
        //是否展示修改行数据弹窗
        updateTableRowDataIsShow: false,
        //当前选中行索引
        currentRowIndex: 0,
        //当前选中行数据
        currentRowData: {},
        tableShowData: [],
        //表中所有字段属性
        allTableField: [],
        tableIndex: 0,
        tableKey: '',
        jsonTables: {},
        columnKeyNamesMap: {},
        columnSelectMap: {},
        schemas: [],
        topologyName: '',
    }

    export default {
        watch: {
            file(newFile) {
                const {realPath: filePath} = newFile
                if (filePath.endsWith("doc") || filePath.endsWith("docx")
                    || filePath.endsWith("xls") || filePath.endsWith("xlsx")
                    || filePath.endsWith("ppt") || filePath.endsWith("pptx")
                    || filePath.endsWith("vsd")) {
                    this.previewFileData = ''
                } else {
                    // this.loadHtml();
                    //alert("暂不支持此文件格式。。。")
                    let fileServerPath = this.configProp.fileServerPath;
                    let previewPath = this.configProp.previewPath;
                    let fileUrl = fileServerPath + '/' + newFile.groups + '/' + newFile.realPath;
                    this.previewFileData = previewPath + encodeURIComponent(fileUrl);
                }
            }
        },
        components: {ICol},
        data() {
            return JSON.parse(JSON.stringify(originData))
        },
        props: {
            //传入的文件信息
            file: {
                type: Object,
                default: {
                    name: '',
                    recommendParserId: ''
                }
            },
            parserData: {
                type: Array,
                default: []
            },
            parserExtList: {
                type: Array,
                default: []
            },
            configProp: {
                type: Object,
                default: {}
            }
        },
        created() {
            var me = this;
            //重新打开页面 清空数据
            Bus.$on('cleanData', () => {
                this.resetData()
                me.fields = [];
                this.jsonStr = "";
                // alert(this.jsonStr);
                this.$refs.result.innerHTML = '';
                if (me.$refs.table.children[0]) {
                    me.$refs.table.removeChild(me.$refs.table.children[0]);
                }
                this.$refs.uploadFile[0].clearFiles();
                this.uploadListFile = [];
                this.resultFils = "";
            });
            Bus.$emit('cleanData', "");
        },
        // beforeDestroy(){
        //     clearData();
        // },
        computed: {
            tableShowColumns() {
                let tableShowColumns = [];
                for (let key in this.columnData) {
                    tableShowColumns.push({
                        title: key,
                        key: key,
                        minWidth: 150
                    })
                }
                return tableShowColumns;
            },
            tableKeysForDisplay() {
                const tableKey = this.tableKey
                let rows = this.jsonTables[tableKey] || []
                const keys = Object.keys(rows[0] || {})
                return keys
            }
        },
        async mounted() {
        },
        methods: {
            submitTopology() {
                const name = this.topologyName
                if (!name) {
                    this.$message.error('请输入图片名称')
                    return
                }
                const json = this.graphEditor.exportJSON()

                const loading = this.$loading({
                    lock: true,
                    text: '正在保存',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                this.$axios.post('mvc/picture/insertData', {
                    name,
                    json:JSON.stringify(json)
                }).then((res) => {  //接口返回数据
                    this.$message.success('保存成功')
                    loading.close()
                }).catch(function (error) {
                    loading.close()
                });
            },
            resetData() {
              Object.keys(originData).forEach(key =>{
                  const copy = JSON.parse(JSON.stringify(originData))
                  const data = copy[key]
                  this[key] = data
              })
            },
            handlePreview() {

                // localStorage.setItem('__needRefresh__', '1')
                // localStorage.getItem('__needRefresh__')
                this.modalPreviewFile = true
                this.$nextTick(() => {
                    this.renderTopology()
                    this.loadJSONData(this.jsonStr)
                })
                // this.$router.push(`tuopu`);
            },

            renderTopology() {
                const that = this
                Q.Editor.prototype.initToolbar = function (toolbar, graph) {

                    Q.createToolbar(graph, toolbar, {
                        editor: [
                            {
                                name: '创建连线',
                                interactionMode: Q.Consts.INTERACTION_MODE_CREATE_EDGE,
                                iconClass: 'q-icon toolbar-edge',
                                styles: {
                                    'arrow.to': false,
                                    'arrow.from': false
                                },
                            },
                            {
                                name: '创建单向连线',
                                interactionMode: Q.Consts.INTERACTION_MODE_CREATE_EDGE,
                                iconClass: 'q-icon toolbar-edge',
                                styles: {
                                    'arrow.to': true,
                                    'arrow.from': false
                                },
                            },
                            {
                                name: '创建双向连线',
                                interactionMode: Q.Consts.INTERACTION_MODE_CREATE_EDGE,
                                iconClass: 'q-icon toolbar-edge',
                                styles: {
                                    'arrow.to': true,
                                    'arrow.from': true
                                },
                            }
                            // {
                            //     name: '创建线条',
                            //     interactionMode: Q.Consts.INTERACTION_MODE_CREATE_LINE,
                            //     iconClass: 'q-icon toolbar-line'
                            // },
                        ]
                    })
                }

                $('#editor').graphEditor({
                    images: {
                        name: '子网设备',
                        images: this.images
                    }, callback: function (editor) {
//      	console.log(editor.exportJSON())
//      	editor.loadDatas()
                        that.graphEditor = editor
                        var toolbox = editor.toolbox;
                        toolbox.hideDefaultGroups();
                        var graph = editor.graph;

                        var propertySheet = editor.propertyPane;
                        propertySheet.showDefaultProperties = false;

                        propertySheet.getCustomPropertyDefinitions = function(data){
                            var id = data.get('id');
                            var properties = that.idPropertiesMap[id] || []
                            return {
                                group: '属性',
                                properties: properties
                            }
                        }
                        //实现带箭头的线条
                        graph.onElementCreated = function (data) {
                            Q.Graph.prototype.onElementCreated.apply(this, arguments);
                            if (this.interactionProperties) {
                                if (this.interactionProperties.styles) {
                                    data.putStyles(this.interactionProperties.styles)
                                }
                                if (this.interactionProperties.properties) {
                                    for (var name in this.interactionProperties.properties) {
                                        data[name] = this.interactionProperties.properties[name]
                                    }
                                }
                            }
                        }
                        graph.moveToCenter();
                    }
                });
            },


            loadJSONData(res) {
                this.$axios.post('mvc/picture/handlePicture', {
                    jsonStr: res
                }).then(res => {
                    this.graphEditor.loadDatas(JSON.parse(res.data))
                });
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

            updateTableRowData() {
                this.$set(this.tableShowData, this.currentRowIndex, this.currentRowData);
            },
            //双击表格行，弹出修改行数据窗口
            tableRowDoubleClick(data, index) {
                this.currentRowIndex = index;
                this.currentRowData = data;
                this.updateTableRowDataIsShow = true;
            },
            getColumns(table_name) {
                this.$axios.post('mvc/listColumns', {
                    tableName: table_name
                }).then(res => {
                    if (res.data) {
                        this.allTableField = res.data;
                        let columnData = {};
                        this.allKey.forEach(key => {

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
                        console.log('+++++++++++++++')
                        console.log(this.columnKeyNamesMap)
                        let selectData = {};
                        for (let key in columnData) {
                            if (this.columnData[key].length > 0) {
                                selectData[key] = columnData[key][0];
                            }
                        }
                        this.selectData = selectData;
                        this.columnData = columnData;
                    }
                })
            },

            getparamList() {
                if (this.file.recommendParserId) {
                    this.$axios.post('mvc/fileParser/getParamList', {
                        parserId: this.file.recommendParserId
                    }).then(res => {
                        this.parserExtList = res.data;

                    });
                }
            },
            //解析处理
            handleParse() {
                const loading = this.$loading({
                    lock: true,
                    text: '正在解析中',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                try {
                    this.parserExtList.forEach(item => {
                        if (item.parameterName.endsWith('File')) {
                            item.parameterValue = this.resultFils
                        }
                    });

                    this.fields = [];
                    if (this.$refs.table.children[0]) {
                        this.$refs.table.removeChild(this.$refs.table.children[0]);
                    }
                    this.$axios.post('mvc/getConfig').then(res => {
                        this.config = res.data;
                        //预览
                        let fileServerPath = this.config.fileServerPath;
                        let previewPath = this.config.previewPath;
                        console.log("_______");
                        console.log(fileServerPath);
                        let fileUrl = fileServerPath + '/' + this.selectFileList[0].groups + '/' + this.selectFileList[0].realPath;
                        this.parseStr = previewPath + encodeURIComponent(fileUrl);
                    }).then(res => {
                        this.$refs.parsejson.innerHTML = '<textarea id="ID"  style="width:100%;height:300px;overflow:scroll;resize:none;" >' + this.parseStr + '</textarea>'
                    });
                } catch (e) {
                    console.log(e)
                }
                this.$axios.post('mvc/fileParser/singleParse', {
                    id: this.file.recommendParserId,
                    params: this.file.id,
                    parserExt: JSON.stringify(this.parserExtList)
                }).then(res => {
                    this.loading = false;
                    if (res.data.success == false) {
                        this.$notify({
                            title: '提示',
                            message: res.data.data,
                            type: 'error'
                        });
                        return;
                    }
                    //展示json结果
                    this.jsonStr = res.data.data.jsonStr;
                    this.$refs.result.innerHTML = '<textarea id="ID"  style="width:100%;height:300px;overflow:scroll;resize:none;" >' + this.jsonStr + '</textarea>'
                    let data = JSON.parse(this.jsonStr);

                    //this.jsonTables = {"table1":[{"host":1,"ceshi":2},{"host":1,"ceshi":2}],"table2":[{"host":1,"ceshi":2,"param":"param"},{"host":1,"ceshi":2,"param":"param"}]};
                    this.jsonTables = JSON.parse(this.jsonStr);


                    //if (data.constructor != Array) {
                    //    return ;
                    // }
                    //this.tableShowData =this.jsonStr == '' ? [] : JSON.parse(this.jsonStr);
                    // this.tableShowData.forEach(item=>{
                    //    for(let key in item){
                    //       item[key.toLowerCase()] = item[key];
                    //     }
                    // });
                    delete res.data.data.jsonStr;
                    //解析匹配到得表名
                    this.table_name = res.data.data.table_name;
                    //根据表名获取该表字段信息
                    this.getColumns(this.table_name);
                    delete res.data.data.table_name;
                    this.allKey = res.data.data.allKey;
                    this.allKeyForDisplay = res.data.data.allKeyForDisplay;

                    delete res.data.data.allKey;
                    //key的映射关系
                    this.columnData = res.data.data;
                    let selectData = {};
                    for (let key in this.columnData) {
                        if (this.columnData[key].length > 0) {
                            selectData[key] = this.columnData[key][0];
                        }
                    }
                    this.selectData = selectData;
                    this.changeDefaultTab()
                }).catch(e => {
                    this.loading = false;
                }).finally(() => {
                    loading.close()

                    this.genParamsByAllKey()
                })
            },
            changeDefaultTab() {
              const defaultKey = Object.keys(this.jsonTables)[0]
                this.toggleTab(0, defaultKey)
            },
            async genParamsByAllKey() {
                // 获取库
                await this.getSchemas()
                console.log('+++++++++++++++')
                console.log(this.schemas)
                this.allKey.forEach(key => {
                    this.$set(this.columnKeyNamesMap, key, {})

                    this.$set(this.columnKeyNamesMap[key], 'schemaId', '')
                    this.$set(this.columnKeyNamesMap[key], 'tableId', '')


                    this.$set(this.columnKeyNamesMap[key], 'columnId', '')
                    this.$set(this.columnSelectMap, key, {})
                    this.$set(this.columnSelectMap[key], 'schemas', this.schemas)
                    this.$set(this.columnSelectMap[key], 'tables', [])
                    this.$set(this.columnSelectMap[key], 'columns', [])
                })

            },
            getSchemas() {
                return this.$axios.post('mvc/getAllSchemas').then(res => {
                    this.schemas = res.data
                })
            },

            getTables(schemaId, key) {
                this.$axios.post('mvc/getTablesBySchemaId', {
                    schemaId: schemaId
                }).then(res => {
                    this.$set(this.columnSelectMap[key], 'tables', res.data)
                    this.$set(this.columnKeyNamesMap[key], 'tableId', '')
                    this.$set(this.columnSelectMap[key], 'columns', res.data)
                    this.$set(this.columnKeyNamesMap[key], 'columnId', '')
                    this.$set(this.columnSelectMap[key], 'dicTables', null)
                    this.$set(this.columnKeyNamesMap[key], 'dicMap', {})
                })
            },

            getColumnsByTable(tableId, key) {
                this.$axios.post('mvc/getColumnsForTable', {
                    tableId
                }).then(res => {
                    const data = res.data || []
                    const dicData = data.filter(v => v.isDic === 0)
                    this.$set(this.columnSelectMap[key], 'columns',dicData)
                    this.$set(this.columnKeyNamesMap[key], 'columnId', '')
                    this.$set(this.columnSelectMap[key], 'dicTables', null)
                    this.$set(this.columnKeyNamesMap[key], 'dicMap', {})
                })
                this.getDicByTableId(tableId, key)
            },
            getDicByColumn(columnId, key) {
                // const column = this.columnSelectMap[key]['columns'].find(c => c.id === columnId)
                // console.log(column)
                // const {isDic, tableId} = column || {}
                //     this.getDicByTableId(tableId, key)
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
                        this.$set(this.columnKeyNamesMap[key]['dicMap'], columnEnglish, '')
                    })
                    this.getDicColumnsByDicName(dicTable,key);
                })

            },

                  getDicColumnsByDicName(dicTable, key) {
                this.$axios.post('mvc/getDicColumnsByDicName', {
                    dicName:dicTable
                }).then(res => {
                    this.$set(this.columnSelectMap[key], 'dicColumns', res.data)
                    console.log(res.data)
                });
                  },

            handleSaveMapInfo() {
            console.log(this.columnKeyNamesMap)
                this.$axios.post('mvc/saveColumnMapInfos', {
                    columnKeyNamesMap:JSON.stringify(this.columnKeyNamesMap),
                    parserId:this.file.recommendParserId
                }).then(res => {
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                })
            },


            toggleTab(index,key) {
                this.tableIndex = index;
                this.tableKey = key
            },
            //下载json文件
            downloadJsonFile() {
                if (!this.jsonStr) {
                    this.$notify({
                        title: '提示',
                        message: 'json内容为空！！',
                        type: 'error'
                    });
                }
                this.$axios.post('mvc/jsonToFile', {
                    json: this.jsonStr
                }, {
                    responseType: 'blob'
                }).then(res => {
                    this.download(res)
                });
            },
            // 下载文件
            download(data) {
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
            //将数据入库
            handleSaveData() {
                let fieldMap = {};
                this.columnData.forEach((field) => {
                    if (field['value']) {
                        fieldMap[field['name']] = field['value']
                    }
                })
                let result = [];
                let keys = Object.keys(fieldMap)
                if (keys.length > 0) {
                    this.tableData.forEach(d => {
                        let obj = {};
                        keys.forEach(k => {
                            obj[k] = d[fieldMap[k]]
                        })
                        result.push(obj)
                    })

                } else {
                    this.$notify({
                        title: '提示',
                        message: '请完善字段对应关系！',
                        type: 'error'
                    });
                    return;
                }
                this.$axios.post('mvc/addData', {
                    mapping: JSON.stringify(fieldMap),
                    result: JSON.stringify(result)
                }).then(res => {
                    if (res.data.success) {
                        this.$emit('after-close')
                    }
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                })
            },


            parseDataSaveDatabase() {
                this.$axios.post('mvc/fileParser/parseDataSaveHBase', {
                    parserId: this.file.recommendParserId,
                    file_id: this.file.id,
                    customKeys: JSON.stringify(this.selectData),
                    columnKeyNamesMap: JSON.stringify(this.columnKeyNamesMap),
                    // table_name: this.table_name,
                    jsonStr: this.jsonStr
                }).then(res => {
                    this.loading = false;
                    this.$notify({
                        title: '提示',
                        message: res.data.data,
                        type: res.data.success ? 'success' : 'error'
                    });
                }).catch(e => {
                    this.loading = false;
                });
            },

            handleUploadFile(file) {
                this.uploadListFile.push(file);
                file.status = 'finished'
                this.$refs.uploadFile[0].fileList.push(file);
                return false;
                console.info("文件地址集合：" + this.uploadListFile);
            },
            uploadSuccessFile(response, file, fileList) {
                console.log(file)
                console.log(fileList)
                this.resultFils = file.response;
                console.info(this.resultFils);
                this.loadingStatus = false;
                this.$Message.success('This is a success tip');
            },

            uploadSuccessFile(response, file, fileList) {
                console.log(file)
                console.log(fileList)
                this.resultFils = file.response;
                console.info(this.resultFils);
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
                console.info(this.uploadListFile.length);
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
            }
        }
 }
</script>
<style>
 /*   .layout {
        position: relative;
        width: 1500px;
        height: 800px;
    }*/
</style>
