<template>
    <div>
        <!--<Row>-->
            <!--<router-link to="/applicationConf"><Button class="tableBtn">application配置</Button></router-link>-->
            <!--<router-link to="/kafkaHbaseConf"><Button class="tableBtn">kafka,hbase配置</Button></router-link>-->
        <!--</Row>-->
        <div class="container">
            <div class="form-box">
            <el-form ref="form" :model="form"  :label-position="labelPosition" label-width="250px" :rules="rules" @click="onSubmitAdd">
                <!--<el-form-item label="BOOTSTRAP_SERVERS：" prop="BOOTSTRAP_SERVERS" >-->
                    <!--<el-input v-model="form.BOOTSTRAP_SERVERS"></el-input>-->
                <!--</el-form-item>-->
                <!--<el-form-item label="GROUP_ID_CONFIG：" prop="GROUP_ID_CONFIG">-->
                    <!--<el-input v-model="form.GROUP_ID_CONFIG"></el-input>-->
                <!--</el-form-item>-->
                <el-form-item label="DEFAULT_TOPIC：" prop="DEFAULT_TOPIC">
                    <el-input v-model="form.DEFAULT_TOPIC"></el-input>
                </el-form-item>
                <!--<el-form-item label="HBASE_ZOOKEEPER_QUORUM：" prop="HBASE_ZOOKEEPER_QUORUM">-->
                    <!--<el-input v-model="form.HBASE_ZOOKEEPER_QUORUM"></el-input>-->
                <!--</el-form-item>-->
                <el-form-item label="schema：" prop="propertySchema">
                    <el-input v-model="form.propertySchema"></el-input>
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
  /*              rules: {
                    BOOTSTRAP_SERVERS: [{required: true, message: '请输入', trigger: 'blur'}],
                    GROUP_ID_CONFIG: [{required: true, message: '请输入', trigger: 'blur'}],
                    DEFAULT_TOPIC: [{required: true, message: '请输入', trigger: 'blur'}],
                    HBASE_ZOOKEEPER_QUORUM: [{required: true, message: '请输入', trigger: 'blur'}]
                },*/
             form:{
                 BOOTSTRAP_SERVERS:"",
                 GROUP_ID_CONFIG:"",
                 DEFAULT_TOPIC:"",
                 HBASE_ZOOKEEPER_QUORUM:"",
                 propertySchema:""
             }
            }
        },

        created() {
            this.getData();
        },
        methods: {
            getData() {
                this.$axios.post('mvc/getConfProperty').then(res => {
                    this.form.BOOTSTRAP_SERVERS = res.data.bootStrapServers;
                    this.form.GROUP_ID_CONFIG = res.data.groupIdConfig;
                    this.form.DEFAULT_TOPIC = res.data.defaultTopic;
                    this.form.HBASE_ZOOKEEPER_QUORUM = res.data.hbaseZookeeperQuorum;
                    this.form.propertySchema=res.data.propertySchema

                });
            },
            async onSubmit() {
                if (!this.form.BOOTSTRAP_SERVERS || !this.form.GROUP_ID_CONFIG || !this.form.DEFAULT_TOPIC || !this.form.HBASE_ZOOKEEPER_QUORUM||!this.form.propertySchema) {
                    this.$message.warning('请填写完整表单')
                    return
                }
                console.log(this.form.id)
                await this.$axios.post('mvc/updateConfProperty', {
                    BOOTSTRAP_SERVERS: this.form.BOOTSTRAP_SERVERS,
                    GROUP_ID_CONFIG: this.form.GROUP_ID_CONFIG,
                    DEFAULT_TOPIC: this.form.DEFAULT_TOPIC,
                    HBASE_ZOOKEEPER_QUORUM: this.form.HBASE_ZOOKEEPER_QUORUM,
                    propertySchema:this.form.propertySchema
                });
                this.$message('修改成功');
                await this.getData();
            }
        }
    }
</script>
