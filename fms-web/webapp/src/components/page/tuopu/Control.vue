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
            <Button @click="handleAdd">上传控件</Button>
            <el-table :data="tableData.slice((currentPage-1)*pageSize,currentPage*pageSize)" border class="table"
                      ref="multipleTable">
                <el-table-column prop="name" align="center" label="控件名称" width="120">
                </el-table-column>
                <el-table-column prop="type" align="center" label="控件类型" width="120">
                </el-table-column>
                <el-table-column prop="image" align="center" label="图片路径" width="240">
                </el-table-column>
                <el-table-column label="操作" align="center" width="240">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑
                        </el-button>
                        <el-button type="text" icon="el-icon-delete" class="red"
                                   @click="handleDelete(scope.$index, scope.row)">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 删除提示框 -->
            <el-dialog title="提示" :visible.sync="delVisible" width="300px" center>
                <div class="del-dialog-cnt">删除不可恢复，是否确定删除？</div>
                <span slot="footer" class="dialog-footer">
                <el-button @click="delVisible = false">取 消</el-button>
                <el-button type="primary" @click="deleteRow">确 定</el-button>
            </span>
            </el-dialog>

            <el-dialog title="新增" :visible.sync="addVisible" width="40%"  :before-close="closeDialog">
                <el-form ref="form" :model="form" label-width="100px">
                    <el-form-item label="控件名称" label-width="100px">
                        <el-input v-model="form.name" style="width:200px;"></el-input>
                    </el-form-item>
                    <el-form-item label-width="100px" label="控件属性" v-for="(item, index) in inputs"
                                  :key="index">
                        <div class="proper-wrap">
                            <el-input :readonly="item.canDelete === false" v-model="item.text" style="width:200px;"></el-input>
                            <i v-if="index === 0" class="el-icon-plus" @click="addInput"></i>
                            <i v-if="index !==0 && item.canDelete !== false" class="el-icon-minus" @click="removeInput(index)"></i>
                        </div>
                    </el-form-item>
                    <el-form-item label="控件类型(一级)" label-width="100px">
                        <el-select v-model="form.parentType" filterable placeholder="请选择" style="width:200px;">
                            <el-option
                                v-for="(menu, menuIdx) in menuList"
                                :key="menu.name"
                                :label="menu.name"
                                :value="menu.name">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="控件类型(二级)" label-width="100px">
                        <el-select @change="onChangeType" v-model="form.type" filterable placeholder="请选择" style="width:200px;">
                            <el-option
                                v-for="(menu, menuIdx) in menuIdChildrenMap[form.parentType]"
                                :key="menu.name"
                                :label="menu.name"
                                :value="menu.name">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="控件上传" label-width="100px">
                        <el-upload
                            ref="elUpload"
                            :class="uploadSuccessState ? 'uploadSuccess': ''"
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
                </el-dialog>

                <el-dialog title="编辑" :visible.sync="editVisible" width="40%">
                    <el-form ref="form" :model="form" label-width="100px">
                        <el-form-item label="控件名称" label-width="100px">
                            <el-input v-model="form.name" style="width:200px;"></el-input>
                        </el-form-item>
                        <el-form-item label-width="100px" :label="`控件属性${index || ''}`" v-for="(item, index) in inputs"
                                      :key="index">
                            <div class="proper-wrap">
                                <el-input v-model="item.propertyChinese" style="width:200px;"></el-input>
                                <i v-if="index === 0" class="el-icon-plus" @click="addInput"></i>
                                <i v-else class="el-icon-minus" @click="removeInput(index)"></i>
                            </div>
                        </el-form-item>
                        <el-form-item label="控件类型(一级)" label-width="100px">
                            <el-select v-model="form.parentType" filterable placeholder="请选择" style="width:200px;">
                                <el-option
                                    v-for="(menu, menuIdx) in menuList"
                                    :key="menu.name"
                                    :label="menu.name"
                                    :value="menu.name">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="控件类型(二级)" label-width="100px">
                            <el-select @change="onChangeType" v-model="form.type" filterable placeholder="请选择" style="width:200px;">
                                <el-option
                                    v-for="(menu, menuIdx) in menuIdChildrenMap[form.parentType]"
                                    :key="menu.name"
                                    :label="menu.name"
                                    :value="menu.name">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="控件上传" label-width="100px">
                            <el-upload
                                ref="elUpload"
                                :class="uploadSuccessState ? 'uploadSuccess': ''"
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
                            <el-button @click="editVisible=false;reset">取 消</el-button>
                            <el-button type="primary" @click="onSubmit('edit')">提交</el-button>
                        </el-form-item>
                    </el-form>

             <!--   <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdd">确 定</el-button>
            </span>-->
            </el-dialog>
            <div class="pagination">
                <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="tableData.length"
                    :page-size="pageSize"
                    :current-page="currentPage"
                    @current-change="handleCurrentChange">
                </el-pagination>
            </div>
            <el-dialog class="uploadImgMask" :visible.sync="dialogVisible">
                <img width="100%" :src="dialogImageUrl" alt="">
            </el-dialog>
        </div>
    </div>
</template>

<script>

    export default {
        data() {
            return {
                tableData: [],
                form: {
                    name: '',
                    parentType: '',
                    type: '',
                    image: ''
                },
                editVisible:false,
                pageSize:20,
                //图片是否上传成功
                uploadSuccessState:false,
                editDialogVisible:false,
                currentPage:1,
                delVisible: false,
                addVisible: false,
                dialogImageUrl: '',
                dialogVisible: false,
                productImgs: [],
                isMultiple: true,
                imgLimit: 1,
                inputs: [
                    {text: ''}
                ],
                menuList: [],
                menuIdChildrenMap: {},
            }
        },
        created() {
            this.getData();
            this.getMenuList()
        },
        watch: {

        },
        methods: {
            onChangeType(type) {
                const children = this.menuIdChildrenMap[this.form.parentType]
                const selectedType = children.find(child => child.name === type)
                const { id } = selectedType || {}
                this.$axios.post('mvc/listColumnsFormasterslave', { masterSlaveId: id }).then((res) => {

                    let { data } = res
                    data = data || []
                    data.forEach(item => {
                        const {column} = item
                        const { columnChinese, columnEnglish } = column
                           this.inputs.push({
                            text: columnChinese,
                            canDelete: false,
                            ...item
                        })
                    })
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //关闭清空数据
            closeDialog(done){
                        done();
                        this.productImgs = [];
                        this.inputs = [
                            {text: ''}
                        ];
                        this.form = {
                            name: '',
                            type: '',
                        };
            },
            getMenuList() {
                this.$axios.post('mvc/getMenuListFormasterslave').then((res) => {
                    const { data } = res
                    this.menuList = data
                    data.forEach((item) => {
                        this.$set(this.menuIdChildrenMap, item.name, item.children)
                    })
                    console.log(this.menuIdChildrenMap)
                }).catch(function (error) {
                    console.log(error);
                });
            },
            handleCurrentChange(val) {
                this.currentPage = val;
            },

            //新增

            handleAdd() {
                this.form = {
                    name: "",
                    parentType: '',
                    type: "",
                    imageUrl: ""
                };

                this.addVisible = true;
                this.uploadSuccessState = false;
            },
            handleEdit(index, row) {
                this.idx = index;
                const item = this.tableData[index];
                console.log(row)
                this.form = {
                    id:item.id,
                    name: item.name,
                    parentType: item.parentType,
                    type: item.type,
                    imageUrl: item.image
                };
                if(item.image) {
                    this.uploadSuccessState = true;
                }
                this.productImgs = [{url: item.image}];
                this.editVisible = true;
            },
            async getData() {
                let {data} = await this.$axios.post('mvc/control/getList');
                console.log(data, '初始化数据');
                this.tableData = data;
                // this.inputs = data[0].cpList;
            },

            handleDelete(index, row) {
                this.idx = index;
                this.delVisible = true;
            },

            // 确定删除
            async deleteRow(){
                await this.$axios.post('mvc/control/delControl', {id: this.tableData[this.idx].id});
                await this.getData();
                this.$message.success('删除成功');
                this.delVisible = false;
            },

            handleRemove(file, fileList) {//移除图片
            this.uploadSuccessState = false;
                console.log(file, fileList);
            },
            handlePictureCardPreview(file) {//预览图片时调用
                console.log(file);
                this.dialogImageUrl = file.url;
                this.dialogVisible = true;
            },
            handleEditPictureCardPreview(file) {//预览图片时调用
                console.log(file);
                this.dialogImageUrl = file.url;
                this.editDialogVisible = true;
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
                this.uploadSuccessState = true;
            },
            handleExceed(files, fileList) {//图片上传超过数量限制
                this.$message.error('上传图片只能一张!');
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

            //提交

            onSubmit(s_type) {
                const {name, type, imageUrl} = this.form;
                let tableData = this.tableData;

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
                var url,params ;
                var params = {
                    name: this.form.name,
                    type: this.form.type,
                    type1: this.form.parentType,
                    properties: JSON.stringify(this.inputs),
                    url: this.form.imageUrl
                }
                //添加
                if(s_type == 'add'){
                    url = "mvc/control/add";
                    for(let i = 0,len = tableData.length; i < len; i++) {
                        if(tableData[i].name == this.form.name){
                            this.$message.warning('控件名重复');
                            return ;
                        }
                    }
                }else{
                    //修改
                    url = "mvc/control/addControl";
                    params.id = this.form.id;
                }


                this.$axios.post(url, params).then( (result) => {
                    console.log(result, '成功')
                    this.editVisible = false;
                    this.addVisible = false;
                    this.$message.success('提交成功')

                    this.getData();
                    this.reset()
                    loading.close()
                }).catch(e => {
                    console.log(e, '失败')
                    this.editVisible = false;
                    loading.close()
                })
            },
            cancelCrop(){
                this.form = {
                    name: '',
                    type: '',
                };
                this.inputs = [
                    {text: ''}
                ];
                this.productImgs = [];
                this.uploadSuccessState = false;

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
    .uploadSuccess .el-upload.el-upload--picture-card{
        display:none;
    }
    .uploadImgMask .el-dialog__wrapper{
        z-index:999999 !important;
    }
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
