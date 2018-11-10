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
                        <Select v-if="file" v-model="file.recommendParserId" @on-change="getparamList">
                            <Option v-for="item in parserData" :value="item.id" :key="item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>
                </i-col>
                <i-col span="6">
                    <FormItem label="选择表">
                        <Select v-model="table_name" @on-change="getColumns">
                            <Option v-for="item in tableNames" :value="item" :key="item">{{ item }}</Option>
                        </Select>
                    </FormItem>
                </i-col>
                <i-col span="6" prop="parserExtList"  v-for="item in parserExtList">
                    <FormItem :label="item.parameterName">
                        <Input v-if="item.parameterName != 'File'" v-model="item.parameterValue" :placeholder="item.parameterDesc"/>
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
            </Row>
            <Divider>json</Divider>
            <div ref="result"></div>


            <div class="tabs" style="height:20px">
               <div style="float: left;margin-right: 10px; width: 100px;float: left; margin: 3px 5px 2px 3px;border-radius: 3px;font-size: 12px;overflow: hidden;cursor: pointer;height: 23px;line-height: 23px;border: 1px solid #e9eaec;background: #fff;padding: 0 5px 0 12px;vertical-align: middle;color: #666;transition: all .3s ease-in;"
               :class="{tableActive:index == tableIndex}" v-for="(value,key,index) in jsonTables" @click="toggleTab(index)"><a>{{key}}</a></div>
           </div>
         <div>
           <table style="border-right:1px solid #ddd;border-bottom:1px solid #ddd;width:100%" v-for="(value,key,index) in jsonTables" :key="item1" :label="item" :value="item1" v-show="index == tableIndex" border="0" cellspacing="0" cellpadding="0">
                   <thead>
                   <tr>
                       <th style="background: #c0c4cc;border: 1px solid #ddd;" v-for="(value, key) in value[0]">
                            {{key}}
                       </th>
                   </tr>
                   </thead>
                   <tbody>
                   <tr v-for="(value, key) in value">
                       <td v-for="(value, key) in value" style="text-align: center;border-left:1px solid #ddd;border-top:1px solid #ddd">{{value}}</td>
                   </tr>
               </tbody>
              </table>
         </div>



            <Table :columns="tableShowColumns" :data="tableShowData" style="width:100%;overflow-x:scroll;"
                   @on-row-dblclick="tableRowDoubleClick"></Table>
            <Divider> 解析字段</Divider>
            <FormItem v-for="(data,key) in columnData" :label="key" :key="key">
                <Select v-model="selectData[key]" :clearable="true">
                    <Option v-for="(column,index1) in data" :value="column" :key="index1"> {{ column }}</Option>
                </Select>
            </FormItem>
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
        <Divider> 解析数据</Divider>
        <div ref="table" v-show="false"></div>
        <Button type="primary" @click="handleSaveData">入库</Button>
        <Button type="primary" @click="parseDataSaveDatabase">入库测试</Button>
        <Modal
            title="修改数据"
            v-model="updateTableRowDataIsShow"
            @on-ok="updateTableRowData"
            :mask-closable="false">
            <Form label-position="right" :label-width="120">
                <FormItem :label="item.key" v-for="(item,index) in tableShowColumns" :key="index">
                    <Input v-model="currentRowData[item.key]" style="width: 300px"
                           :maxlength="tableInputMaxLength(item.key)"/>
                </FormItem>
            </Form>
        </Modal>
    </div>
</template>
<script>
    import ICol from "../../../../node_modules/iview/src/components/grid/col.vue";
    import Vue from 'vue';
    import Bus from '@/components/common/bus'

    export default {
        components: {ICol},
        data() {
            return {
                uploadListFile:[],
                loadingStatus: false,
                fileList: [],
                resultFils:"",
                loading: false,
                //解析结果字段
                fields: [],
                //解析结果json字符
                jsonStr: '',
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
                 tableIndex:0,
                jsonTables:{}
            }
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
            parserExtList:{
                type: Array,
                default: []
            }
        },
        created() {
            var me = this;
            this.getTables()
            //重新打开页面 清空数据
            Bus.$on('cleanData', () => {

                me.fields = [];
                if (me.$refs.table.children[0]) {
                    me.$refs.table.removeChild(me.$refs.table.children[0]);
                }

                this.$refs.uploadFile[0].clearFiles();
                this.uploadListFile = [];
                this.resultFils = "";

            });
        },
        computed: {
            tableShowColumns() {
                let tableShowColumns = [];
                for (let key in this.columnData) {
                    tableShowColumns.push({
                        title: key,
                        key: key,
                        minWidth:150
                    })
                }
                return tableShowColumns;
            }
        },
        methods: {
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
            getTables() {
                this.$axios.post('mvc/getTables').then(res => {
                    this.tableNames = res.data;
                })
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
            getparamList(){
                if(this.file.recommendParserId){
                    this.$axios.post('mvc/fileParser/getParamList', {
                          parserId:this.file.recommendParserId
                     }).then(res => {
                         this.parserExtList = res.data;
                      });
                 }
            },
            //解析处理
            handleParse() {

                this.parserExtList.forEach(item => {
                    if(item.parameterName.endsWith('File')){
                        item.parameterValue = this.resultFils
                    }
                });

                this.fields = [];
                if (this.$refs.table.children[0]) {
                    this.$refs.table.removeChild(this.$refs.table.children[0]);
                }
                this.$axios.post('mvc/fileParser/singleParse', {
                    id: this.file.recommendParserId,
                    params: this.file.id,
                    parserExt: JSON.stringify(this.parserExtList)
                }).then(res => {
                    this.loading = false;
                    if(res.data.success==false){
                        this.$notify({
                            title: '提示',
                            message: res.data.data,
                            type: 'error'
                        });
                        return;
                    }
                    //展示json结果
                    this.jsonStr = res.data.data.jsonStr;
                    this.$refs.result.innerHTML = '<textarea id="ID"  style="width:100%;height:300px;overflow:scroll;resize:none;" >'+ this.jsonStr +'</textarea>'
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
                }).catch(e => {
                    this.loading = false;
                });
            },
            toggleTab(index){
                this.tableIndex = index;
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
                let validate=true;
                //校验字段映射关系是否冲突
                let data1 = this.selectData;
                let data2 = this.selectData;
                for (let key1 in data1) {
                    for (let key2 in data2) {
                        if (data1[key1] == data2[key2] && key1 != key2 && data1[key1] != undefined && data2[key2] != undefined) {
                            this.$notify({
                                title: '提示',
                                message: '【' + key1 + '】和【' + key2 + '】映射关系重复',
                                type: 'error'
                            });
                            return;
                        }
                    }
                }
                //校验值长度是否合法
                this.tableShowData.forEach((row, index) => {
                    for (let column in row) {
                        this.allTableField.forEach(field => {
                            let realColumn = this.selectData[column];
                            //校验长度
                            if (field.max_length!=undefined&&field.column_name == realColumn && row[column].length > field.max_length&&validate) {
                                this.$notify({
                                    title: '提示',
                                    message: '第' + (index + 1) + '行字段' + column + '长度超出，最大长度' + field.max_length + '当前长度' + row[column].length,
                                    type: 'error'
                                });
                                validate=false;
                            }
                            //校验日期类型是否合法
                            if (field.column_name == realColumn && (field.data_type=='date'||field.data_type=='datetime')&&validate) {
                                if(!/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/.test(row[column])){
                                    this.$notify({
                                        title: '提示',
                                        message: '第' + (index + 1) + '行字段' + column + '类型错误，期望类型:' + field.data_type,
                                        type: 'error'
                                    });
                                    validate=false;
                                }
                            }
                            //校验整形是否合法
                            if (field.column_name == realColumn && (field.data_type=='int'||field.data_type=='bigint')&&validate) {
                                if(!/^-?[1-9]\d*$/.test(row[column])){
                                    this.$notify({
                                        title: '提示',
                                        message: '第' + (index + 1) + '行字段' + column + '类型错误，期望类型:' + field.data_type,
                                        type: 'error'
                                    });
                                    validate=false;
                                }
                            }
                            //校验浮点型是否合法
                            if (field.column_name == realColumn && (field.data_type=='int'||field.data_type=='bigint')&&validate) {
                                if(!/^(([1-9]\d*)|0)(\.\d{1-2})?$/.test(row[column])){
                                    this.$notify({
                                        title: '提示',
                                        message: '第' + (index + 1) + '行字段' + column + '类型错误，期望类型:' + field.data_type,
                                        type: 'error'
                                    });
                                    validate=false;
                                }
                            }
                        })
                    }
                })
                if(!validate){
                    return;
                }
                this.loading = true;
                this.$axios.post('mvc/fileParser/parseDataSaveDatabase', {
                    parserId: this.file.recommendParserId,
                    file_id: this.file.id,
                    customKeys: JSON.stringify(this.selectData),
                    table_name: this.table_name,
                    jsonStr: JSON.stringify(this.tableShowData)
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

                        handleUploadFile(file){
                            this.uploadListFile.push(file);
                            file.status = 'finished'
                            this.$refs.uploadFile[0].fileList.push(file);
                            return false;
                            console.info("文件地址集合："+this.uploadListFile);
                        },

                        uploadSuccessFile(response, file, fileList){
                            console.log(file)
                            console.log(fileList)
                            this.resultFils = file.response;
                            console.info(this.resultFils);
                            this.loadingStatus = false;
                            this.$Message.success('This is a success tip');
                        },

                        uploadSuccessFile(response, file, fileList){
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

                        uploadErrorFile(){
                            this.loadingStatus = false;
                            this.$Message.error('上传文件失败');
                        },

                        FileUpload(){
                            console.info(this.uploadListFile.length);
                            let length = this.uploadListFile.length;
                            if(length != 1){
                                this.$Message.error('有且只能上传一个文件');
                                return false;
                            }
                            this.loadingStatus = true;
                            let FileNameList=[];
                            this.$refs.uploadFile[0].clearFiles();
                                if (this.uploadListFile) {
                                    this.uploadListFile.forEach(file => {
                                        this.$refs.uploadFile[0].post(file);
                                    })
                                }

                        },
        }
    }
</script>
