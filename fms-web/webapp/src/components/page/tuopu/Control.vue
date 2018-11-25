<template>
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
        <div class="container">
            <div class="form-box">
                <el-form ref="form" :model="form" label-width="100px">
                    <el-form-item label="控件名称" label-width="100px">
                        <el-input v-model="form.name" style="width:200px;"></el-input>
                    </el-form-item>
                    <el-form-item label-width="100px" :label="`控件属性${index || ''}`" v-for="(item, index) in inputs"
                                  :key="index">
                        <div class="proper-wrap">
                            <el-input v-model="item.text" style="width:200px;"></el-input>
                            <i v-if="index === 0" class="el-icon-plus" @click="addInput"></i>
                            <i v-else class="el-icon-minus" @click="removeInput(index)"></i>
                        </div>
                    </el-form-item>
                    <el-form-item label="控件类型" label-width="100px">
                        <el-select v-model="form.type" placeholder="请选择" style="width:200px;">
                            <el-option key="bbk" label="网络" value="bbk"></el-option>
                            <el-option key="xtc" label="硬件" value="xtc"></el-option>
                            <el-option key="imoo" label="区块" value="imoo"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="控件上传" label-width="100px">
                        <el-upload
                            ref="elUpload"
                            action="mvc/control/imgUpload"
                            list-type="picture-card"
                            accept="image/*"
                            :limit="imgLimit"
                            :file-list="productImgs"
                            :multiple="isMultiple"
                            :on-preview="handlePictureCardPreview"
                            :on-remove="handleRemove"
                            :on-success="handleUploadSuccess"
                            :before-upload="beforeAvatarUpload"
                            :on-exceed="handleExceed"
                            :on-error="imgUploadError">
                            <i class="el-icon-plus"></i>
                        </el-upload>
                        <!--    <el-dialog :visible.sync="dialogVisible">
                                <img width="100%" :src="dialogImageUrl" alt="">
                            </el-dialog>
    -->
                        <!--    <el-upload
                                class="avatar-uploader"
                                action="mvc/sd"
                                :show-file-list="false"
                                :on-success="handleAvatarSuccess"
                                :before-upload="beforeAvatarUpload">
                                <img v-if="imageUrl" :src="imageUrl" class="avatar">
                                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                            </el-upload>-->
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="cancelCrop">取 消</el-button>
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
                form: {
                    name: '',
                    type: '',
                    imageUrl: ''
                },
                dialogImageUrl: '',
                dialogVisible: false,
                productImgs: [],
                isMultiple: true,
                imgLimit: 6,
                inputs: [
                    {text: ''}
                ],
            }
        },
        methods: {
            handleRemove(file, fileList) {//移除图片
                console.log(file, fileList);
            },
            handlePictureCardPreview(file) {//预览图片时调用
                console.log(file);
                this.dialogImageUrl = file.url;
                this.dialogVisible = true;
            },

            beforeAvatarUpload(file) {//文件上传之前调用做一些拦截限制
                console.log(file);
                const isJPG = true;
                // const isJPG = file.type === 'image/jpeg';
                const isLt2M = file.size / 1024 / 1024 < 2;

                // if (!isJPG) {
                //   this.$message.error('上传头像图片只能是 JPG 格式!');
                // }
                if (!isLt2M) {
                    this.$message.error('上传图片大小不能超过 2MB!');
                }
                return isJPG && isLt2M;
            },
            handleUploadSuccess(res, file) {//图片上传成功
                const {success, error, url} = res
                if (!success) {
                    this.$message.error(error);
                    this.$refs.elUpload.clearFiles()
                    return
                }
                this.form.imageUrl = url;
            },
            handleExceed(files, fileList) {//图片上传超过数量限制
                this.$message.error('上传图片不能超过6张!');
                console.log(file, fileList);
            },
            imgUploadError(err, file, fileList) {//图片上传失败调用
                console.log(err);
                this.$message.error('上传图片失败!');
            },
            addInput() {
                this.inputs.push({
                    text: ''
                })
            },
            removeInput(index) {
                this.inputs.splice(index, 1)
            },
            onSubmit() {
                const {name, type, imageUrl} = this.form
                const inputsValid = this.inputs.some(v => {
                    return v.text !== ''
                })
                if (!name || !type || !inputsValid || !imageUrl) {
                    this.$message.warning('请填写完整表单, 并上传图片')
                    return
                }

                this.inputs = this.inputs.filter(v => {
                    return !!v.text
                })

                const loading = this.$loading({
                    lock: true,
                    text: '正在保存',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                this.$axios.post("mvc/control/add", {
                    name: this.form.name,
                    type: this.form.type,
                    properties: JSON.stringify(this.inputs),
                    url: this.form.imageUrl
                }).then(function (result) {
                    this.$message.success('提交成功')
                    this.reset()
                    loading.close()
                }).catch(e => {
                    loading.close()
                })
            },
            reset() {
                this.form = {
                    name: '',
                    type: '',
                }
                this.inputs = [
                    {text: ''}
                ]
            }
        }
    }

</script>

<style>
    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }

    .avatar-uploader .el-upload:hover {
        border-color: #409EFF;
    }

    .el-upload__input {
        display: none !important;
    }

    .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        line-height: 178px;
        text-align: center;
    }

    /* .avatar {
         cancelCrop width: 178px;
         height: 178px;
         display: block;
     }
 */
    .proper-wrap {
        margin-bottom: 12px;
    }
</style>
