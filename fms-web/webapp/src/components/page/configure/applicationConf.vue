<template>
    <div>
        <Row>
            <router-link to="/applicationConf"><Button class="tableBtn">application配置</Button></router-link>
            <router-link to="/kafkaHbaseConf"><Button class="tableBtn">kafka,hbase配置</Button></router-link>
        </Row>
        <div class="container">
            <div class="form-box">
            <el-form ref="form" :model="form"  :label-position="labelPosition" label-width="150px" :rules="rules" @click="onSubmitAdd">
                <el-form-item label="parserPath：" prop="parserPath" >
                    <el-input v-model="form.parserPath"></el-input>
                </el-form-item>
                <el-form-item label="fileTmpPath：" prop="fileTmpPath">
                    <el-input v-model="form.fileTmpPath"></el-input>
                </el-form-item>
                <el-form-item label="url：" prop="url">
                    <el-input v-model="form.url"></el-input>
                </el-form-item>
                <el-form-item label="fileUploadPath：" prop="fileUploadPath">
                    <el-input v-model="form.fileUploadPath"></el-input>
                </el-form-item>
                <el-form-item label="posyspath：" prop="posyspath">
                    <el-input v-model="form.posyspath"></el-input>
                </el-form-item>
                <el-form-item label="popassword：" prop="popassword">
                    <el-input v-model="form.popassword"></el-input>
                </el-form-item>
                <el-form-item label="poolTotal：" prop="poolTotal">
                    <el-input v-model="form.poolTotal"></el-input>
                </el-form-item>
                <el-form-item label="poolMaxIdle：" prop="poolMaxIdle">
                    <el-input v-model="form.poolMaxIdle"></el-input>
                </el-form-item>
                <el-form-item label="connectTimeout：" prop="connectTimeout">
                    <el-input v-model="form.connectTimeout"></el-input>
                </el-form-item>
                <el-form-item label="networkTimeout：" prop="networkTimeout">
                    <el-input v-model="form.networkTimeout"></el-input>
                </el-form-item>
                <el-form-item label="trackerHttpPort：" prop="trackerHttpPort">
                    <el-input v-model="form.trackerHttpPort"></el-input>
                </el-form-item>
           <el-form-item>
              <el-button type="primary" @click="onSubmit">提交</el-button>
           </el-form-item>
         </el-form>
        </div>
        </div>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                labelPosition:"left",
                pageSize: 10,
                currentPage: 1,
                rules: {
                    parserPath: [{required: true, message: '请输入', trigger: 'blur'}],
                    fileTmpPath: [{required: true, message: '请输入', trigger: 'blur'}],
                    url: [{required: true, message: '请输入', trigger: 'blur'}],
                    fileUploadPath: [{required: true, message: '请输入', trigger: 'blur'}],
                    posyspath: [{required: true, message: '请输入', trigger: 'blur'}],
                    popassword: [{required: true, message: '请输入', trigger: 'blur'}],
                    poolTotal: [{required: true, message: '请输入', trigger: 'blur'}],
                    poolMaxIdle: [{required: true, message: '请输入', trigger: 'blur'}],
                    connectTimeout: [{required: true, message: '请输入', trigger: 'blur'}],
                    networkTimeout: [{required: true, message: '请输入', trigger: 'blur'}],
                    trackerHttpPort: [{required: true, message: '请输入', trigger: 'blur'}]
                },
             form:{
                 parserPath:"",
                 fileTmpPath:"",
                 url:"",
                 fileUploadPath:"",
                 posyspath:"",
                 popassword:"",
                 poolTotal:"",
                 poolMaxIdle:"",
                 connectTimeout:"",
                 networkTimeout:"",
                 trackerHttpPort:""
             }
            }
        },

        created() {
            this.getData();
        },
        methods: {
            getData() {
                this.$axios.post('mvc/confProperty/applicationConf').then(res => {
                    console.log(res);
                    this.form.parserPath = res.data.parserPath;
                    this.form.fileTmpPath = res.data.fileTmpPath;
                    this.form.url = res.data.url;
                    this.form.fileUploadPath = res.data.fileUploadPath;
                    this.form.posyspath = res.data.posyspath;
                    this.form.popassword = res.data.popassword;
                    this.form.poolTotal = res.data.poolTotal;
                    this.form.poolMaxIdle = res.data.poolMaxIdle;
                    this.form.connectTimeout = res.data.connectTimeout;
                    this.form.networkTimeout = res.data.networkTimeout;
                    this.form.trackerHttpPort = res.data.trackerHttpPort
                });
            },
            async onSubmit() {
                if (!this.form.parserPath || !this.form.fileTmpPath || !this.form.url || !this.form.fileUploadPath || !this.form.posyspath || !this.form.popassword || !this.form.poolTotal || !this.form.poolMaxIdle || !this.form.connectTimeout || !this.form.networkTimeout || !this.form.trackerHttpPort) {
                    this.$message.warning('请填写完整表单')
                    return
                }

                await this.$axios.post('mvc/confProperty/updateApplicationConf', {
                    parserPath: this.form.parserPath,
                    fileTmpPath: this.form.fileTmpPath,
                    url: this.form.url,
                    fileUploadPath: this.form.fileUploadPath,
                    posyspath: this.form.posyspath,
                    popassword: this.form.popassword,
                    poolTotal: this.form.poolTotal,
                    poolMaxIdle: this.form.poolMaxIdle,
                    connectTimeout: this.form.connectTimeout,
                    networkTimeout: this.form.networkTimeout,
                    trackerHttpPort: this.form.trackerHttpPort
                });
                await this.getData();
            }
        }
    }
</script>
