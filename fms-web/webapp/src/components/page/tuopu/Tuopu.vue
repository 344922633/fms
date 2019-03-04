<template>
    <div>
        <div>
            <Row>
                <router-link to="/tuopu">
                    <Button class="tableBtn">拓扑图</Button>
                </router-link>
                <router-link to="/tuopuManage">
                    <Button class="tableBtn">拓扑图管理</Button>
                </router-link>
                <router-link to="/control">
                    <Button class="tableBtn">控件管理</Button>
                </router-link>
            </Row>
        </div>
        <div class="wrap">
            <div class="layout">
                <div id="editor" data-options="region:'center'"></div>
            </div>
        </div>
        <div class="form-wrap">
            <label>图片名称：</label>
            <input class="save-input" v-model="saveName" style="width:75px;height:30px"/>
            <input class="save-btn" @click="submit" type="button" value="保存" style="width:60px;height:30px"/>
            <input class="save-btn" @click="sendInfo" type="button" value="发送拓扑图信息" style="width:150px;height:30px"/>
        </div>
    </div>
</template>

<script>
    const initProperties = (arr) => {
        const ret = []
        arr.forEach(item => {

            const {
                propertyFlag,
                property,
                propertyChinese,
                isDic,
                dicList,
                tableId,
                groupName

            } = item

            const propertyOption = {
                groupName:groupName,
                tableId:tableId,
                displayName: propertyChinese,
                client:property
            }

            if (isDic === 1 && dicList) {
                let dicArr
                try {
                    dicArr = JSON.parse(dicList)
                }catch (e) {
                    dicArr = []
                }
                // const options = dicArr.map(dic => dic.MC);
                const options = dicArr.map(function(item){
                    return {
                        value:item.MC,
                        key:item.DM
                    }
                })

                propertyOption.options = options

            }
            ret.push(propertyOption)
        })

        return ret
    }

    export default {
        data() {
            return {
                graph: null,
                    saveName: '',
                idPropertiesMap: {}
            }
        },
        created() {
          const needRefresh = localStorage.getItem('__needRefresh__')
          if (needRefresh) {
              localStorage.setItem('__needRefresh__', '')
              location.reload()
          }
        },
        async mounted() {
            // 获取控件
            await this.getControlData()
            await this.getPropertyData()
            this.renderTopology()
            this.getQueryData()
        },
        methods: {

            // 获取控件
            getControlData() {
                return this.$axios.post('mvc/control/getList').then((res) => {  //接口返回数据
                    let { data } = res
                    data.forEach((d,i) => {

                        // d.image = '/static/img/img.jpg'
                        d.label = d.name
                        d.clientProperties ={
                            id: d.id
                        }

                        if(i==0){
                             d.type = 'Group'
                        }else{
                            delete d.type
                        }

                    })
                    this.images = data
                }).catch(function (error) {
                    console.log(error);
                });
            },
            getPropertyData() {
                return this.$axios.post('mvc/getControlPropertyList').then((res) => {  //接口返回数据
                    const { data } = res
                    let ret = {}
                    data.forEach(item => {
                        ret[item.controlId] = initProperties(item.properties)
                    })

                    this.idPropertiesMap = ret

                }).catch(function (error) {
                    console.log(error);
                });
            },
            renderTopology() {
                const that = this

                Q.Editor.prototype.initToolbar = function (toolbar, graph) {
                    var exportButtons = [{
                        name: getI18NString('Export JSON'), iconClass: 'q-icon toolbar-json', action: this.showJSONPanel.bind(this)
                    }
                    ]
                    if (Q.isFileSupported) {
                        exportButtons.push({
                            iconClass: 'q-icon toolbar-upload',
                            name: getI18NString('Load File ...'), action: this.loadJSONFile.bind(this), type: 'file'
                        })
                    }
                    if (window.saveAs) {
                        exportButtons.push({
                            iconClass: 'q-icon toolbar-download',
                            name: getI18NString('Download File'), action: this.exportJSONFile.bind(this, window.saveAs)
                        })
                    }
                    if (this.options.saveService) {
                        exportButtons.push({
                            iconClass: 'q-icon toolbar-save',
                            name: getI18NString('Save'), action: this.save.bind(this)
                        })
                    }
                    Q.createToolbar(graph, toolbar, {
                        export: exportButtons,
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
                                properties: properties,
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
            getQueryData() {
              const id = this.$route.query.id
                this.$axios.post('mvc/picture/showPicture',{id}).then((res) => {
                    const {data} = res

                    if (data) {
                        const {json, name} = data
                        this.loadJSONData(json)
                        this.saveName = name
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
/*
            getQueryData() {
                const id = this.$route.query.id
                this.$axios.post('mvc/picture/handlePicture',{id}).then((res) => {
                    const {data} = res
                    if (data) {
                        const {json} = data
                        this.loadJSONData(json)
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },*/
            loadJSONData(res) {
                try{
                    res = JSON.parse(res)
                }catch (e) {
                    this.$message.error('数据解析失败')
                    res = null
                }
                if (res) {
                    this.graphEditor.loadDatas(res)
                }
            },
            // 保存
            submit() {
                const id = this.$route.query.id
                const name = this.saveName
                if (!name) {
                    this.$message.error('请输入图片名称')
                    return
                }
                const json = this.graphEditor.exportJSON()


                //  if (JSON.stringify(json).search("Group") != -1) {
                //         this.$message.error('没有父组件')
                //         return
                //     }
                const loading = this.$loading({
                    lock: true,
                    text: '正在保存',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                this.$axios.post('mvc/picture/insertData', {
                    id,
                    name,
                    json:JSON.stringify(json)
                }).then((res) => {  //接口返回数据

                    if(this.Data = res.data.success){
                        this.$message.success('保存成功')
                    }else{
                        alert("名称重复")
                    }

                    loading.close()
                }).catch(function (error) {
                    loading.close()
                    console.log(error);
                });
            },

            init(graph, editor) {
                editor.toolbox.hideDefaultGroups();
                editor.toolbox.hideButtonBar();
            },

            sendInfo(){
                const json = this.graphEditor.exportJSON()

                this.$axios.post('mvc/picture/sendDataToKafka', {
                    json:JSON.stringify(json)

                }).then((res) => {  //接口返回数据
                    this.$message.success('发送成功')
                });
            }
        }
    }
</script>

<style>
    .wrap {
        display: flex;
    }

    .form-wrap {
        display: flex;
        position: absolute;
        left: 800px;
        top: 40px;
        width: 400px;
    }

    .form-wrap .save-input {
        width: 240px;
    }
    .form-wrap .save-btn {
        width: 50px;
        margin-left: 8px;
    }

    .layout {
        position: relative;
        width: 100%;
        height: 800px;
    }

    .layout .graph-editor__toolbox {
        height: 100% !important;
    }
</style>
