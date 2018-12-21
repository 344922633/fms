<template>
    <div>
        <Row>
            <router-link to="/applicationConf"><Button class="tableBtn">application配置</Button></router-link>
            <router-link to="/kafkaHbaseConf"><Button class="tableBtn">kafka,hbase配置</Button></router-link>
        </Row>
        <div class="container">
            <div class="form-box">
            <el-form ref="form" :model="form"  :label-position="labelPosition" label-width="250px" :rules="rules" @click="onSubmitAdd">
                <el-form-item label="BOOTSTRAP_SERVERS：" prop="BOOTSTRAP_SERVERS" >
                    <el-input v-model="form.BOOTSTRAP_SERVERS"></el-input>
                </el-form-item>
                <el-form-item label="GROUP_ID_CONFIG：" prop="GROUP_ID_CONFIG">
                    <el-input v-model="form.GROUP_ID_CONFIG"></el-input>
                </el-form-item>
                <el-form-item label="DEFAULT_TOPIC：" prop="DEFAULT_TOPIC">
                    <el-input v-model="form.DEFAULT_TOPIC"></el-input>
                </el-form-item>
                <el-form-item label="HBASE_ZOOKEEPER_QUORUM：" prop="HBASE_ZOOKEEPER_QUORUM">
                    <el-input v-model="form.HBASE_ZOOKEEPER_QUORUM"></el-input>
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
                    BOOTSTRAP_SERVERS: [{required: true, message: '请输入', trigger: 'blur'}],
                    GROUP_ID_CONFIG: [{required: true, message: '请输入', trigger: 'blur'}],
                    DEFAULT_TOPIC: [{required: true, message: '请输入', trigger: 'blur'}],
                    HBASE_ZOOKEEPER_QUORUM: [{required: true, message: '请输入', trigger: 'blur'}]
                },
             form:{
                 BOOTSTRAP_SERVERS:"",
                 GROUP_ID_CONFIG:"",
                 DEFAULT_TOPIC:"",
                 HBASE_ZOOKEEPER_QUORUM:""
             }
            }
        },

        created() {
            this.getData();
        },
        methods: {
            getData() {
                this.$axios.post('mvc/confProperty/kafkaHbaseConf').then(res => {
                    this.form.BOOTSTRAP_SERVERS = res.data.BOOTSTRAP_SERVERS;
                    this.form.GROUP_ID_CONFIG = res.data.GROUP_ID_CONFIG;
                    this.form.DEFAULT_TOPIC = res.data.DEFAULT_TOPIC;
                    this.form.HBASE_ZOOKEEPER_QUORUM = res.data.HBASE_ZOOKEEPER_QUORUM
                });
            },
            async onSubmit() {
                if (!this.form.BOOTSTRAP_SERVERS || !this.form.GROUP_ID_CONFIG || !this.form.DEFAULT_TOPIC || !this.form.HBASE_ZOOKEEPER_QUORUM) {
                    this.$message.warning('请填写完整表单')
                    return
                }

                await this.$axios.post('mvc/confProperty/updateKafkaHbaseConf', {
                    BOOTSTRAP_SERVERS: this.form.BOOTSTRAP_SERVERS,
                    GROUP_ID_CONFIG: this.form.GROUP_ID_CONFIG,
                    DEFAULT_TOPIC: this.form.DEFAULT_TOPIC,
                    HBASE_ZOOKEEPER_QUORUM: this.form.HBASE_ZOOKEEPER_QUORUM
                });
                await this.getData();
            }
        }
    }
</script>
