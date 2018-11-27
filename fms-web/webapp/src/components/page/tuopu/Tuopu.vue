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
            <input class="save-input" v-model="saveName" style="width:75px"/>
            <input class="save-btn" @click="submit" type="button" value="保存" />
        </div>
    </div>
</template>

<script>
    const initProperties = (arr) => {
        const ret = []
        arr.forEach(item => {
            ret.push({
                displayName: item.property,
                client:item.property
            })
        })
        console.log(ret)
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
                    data.forEach(d => {
                        // d.image = '/static/img/img.jpg'
                        d.label = d.name
                        d.clientProperties ={
                            id: d.id
                        }
                        delete d.type
                    })
                    this.images = data
                }).catch(function (error) {
                    console.log(error);
                });
            },
            getPropertyData() {
                return this.$axios.post('mvc/controlProperty/getList').then((res) => {  //接口返回数据
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
                    this.$message.success('保存成功')
                    loading.close()
                }).catch(function (error) {
                    loading.close()
                    console.log(error);
                });
            },
            init(graph, editor) {
                editor.toolbox.hideDefaultGroups();
                editor.toolbox.hideButtonBar();
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
        width: 300px;
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
        width: 1200px;
        height: 800px;
    }

    .layout .graph-editor__toolbox {
        height: 100% !important;
    }
</style>
