<template>
    <div>
        <uploader :options="options" :file-status-text="statusText" class="uploader-example" ref="uploader"
                  @file-complete="fileComplete" @complete="complete" @file-success="fileSuccess"></uploader>

        <!--<ly-tree :data="treeData" :before-add="handleBeforeAdd" :before-del="handleBeforeDel" :before-update="handleBeforeUpdate"
            @node-click="handleNodeClick"></ly-tree>-->
    </div>
</template>

<script>
//    import axios from 'axios'
    import qs from 'qs'
    import LyTree from '@/components/common/vue-tree/tree.vue'
    export default {
        components: {
            LyTree
        },
        data() {
            return {
                treeData: [
                    {
                        "id": 1,
                        "name": "技术部1",
                        "level": 1,
                        "child": [
                            {
                                "id": 2,
                                "name": "运维组",
                                "level": 2,
                                "child": [
                                    {
                                        "id": 3,
                                        "name": "godo",
                                        "level": 3,
                                        "child": []
                                    }
                                ]
                            },
                            {
                                "id": 4,
                                "name": "测试组",
                                "level": 2,
                                "child": []
                            }
                        ]
                    }
                ],
                options: {
                    target: process.env.BASE_UPLOAD + 'mvc/chunk',
//                    target:'mvc/chunk',
                    testChunks: true,
                    simultaneousUploads: 1,
                    chunkSize: 10 * 1024 * 1024
                },
                attrs: {
                    accept: 'image/*'
                },
                statusText: {
                    success: '成功了',
                    error: '出错了',
                    uploading: '上传中',
                    paused: '暂停中',
                    waiting: '等待中'
                }
            }
        },
        methods: {
            // 上传完成
            complete() {
                console.log('complete', arguments)
            },
            fileSuccess(rootFile, file, message, chunk) {
//                const file = arguments[0].file;
                // //localhost:8088/fms/mvc/uploader/mergeFile
                debugger
                this.$axios.post('mvc/mergeFile', {
                    filename: file.name,
                    identifier: file.uniqueIdentifier,
                    totalSize: file.size,
                    type: file.type,
                    location: rootFile.path
                }).then(function (response) {
                    console.log(response);
                }).catch(function (error) {
                    console.log(error);
                });
            },
            // 一个根文件（文件夹）成功上传完成。
            fileComplete(rootFile) {
                console.log('file complete', arguments)
                console.log(rootFile)

            },
            handleBeforeAdd(a, b) {
                console.log(a)
                console.log(b)
            },
            handleBeforeUpdate(a, b) {
                console.log(a)
                console.log(b)
            },
            handleBeforeDel(c) {
                console.log(c)
                return false;
            },
            handleNodeClick(a, b, c) {
                console.log(a)
                console.log(b)
                console.log(c)
            }
        },
        mounted() {
            this.$nextTick(() => {
                window.uploader = this.$refs.uploader.uploader
            })
        }
    }
</script>
