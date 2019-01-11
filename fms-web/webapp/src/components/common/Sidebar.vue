<template>
    <div class="sidebar">
        <el-menu class="sidebar-el-menu" :default-active="onRoutes" :collapse="collapse" background-color="#1c242f"
                 text-color="#bfcbd9" active-text-color="#20a0ff" unique-opened router>
            <template v-for="item in items">
                <template v-if="item.subs">
                    <el-submenu :index="item.index" :key="item.index">
                        <template slot="title">
                            <i :class="item.icon"></i><span slot="title">{{ item.title }}</span>
                        </template>
                        <template v-for="subItem in item.subs">
                            <el-submenu v-if="subItem.subs" :index="subItem.index" :key="subItem.index">
                                <template slot="title">{{ subItem.title }}</template>
                                <el-menu-item v-for="(threeItem,i) in subItem.subs" :key="i" :index="threeItem.index">
                                    {{ threeItem.title }}
                                </el-menu-item>
                            </el-submenu>
                            <el-menu-item v-else :index="subItem.index" :key="subItem.index">
                                {{ subItem.title }}
                            </el-menu-item>
                        </template>
                    </el-submenu>
                </template>
                <template v-else>
                    <el-menu-item :index="item.index" :key="item.index">
                        <i :class="item.icon"></i><span slot="title">{{ item.title }}</span>
                    </el-menu-item>
                </template>
            </template>
        </el-menu>
    </div>
</template>

<script>
    import bus from '../common/bus';
    export default {
        data() {
            return {
                collapse: false,
                items: [
                    {
                        icon: 'el-icon-lx-home',
                        index: 'dashboard',
                        title: '监控统计信息'
                    },
                    {
                        icon: 'el-icon-lx-copy',
                        index: '2',
                        title: '人工录入',
                        subs:[
                            {
                                index: 'manualDataDeal',
                                title: '表单录入',
                                icon: 'el-icon-edit'
                            },
                            {
                                index: 'tuopu',
                                title: '拓扑图管理',
                                icon: 'el-icon-lx-pic'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-lx-copy',
                        index: '3',
                        title: '在线/离线文件导入',
                        subs:[

                            {
                                index: 'fileInput',
                                title: '离线文件导入',
                                icon: 'el-icon-upload'
                            },
                            {
                                index: 'sendFileToServer',
                                title: '上报服务器',
                                icon: 'el-icon-setting'
                            },
                        ]
                    },
                    {
                        icon: 'el-icon-lx-copy',
                        index: '4',
                        title: '素材汇集',
                        subs:[
                            {
                                icon: 'el-icon-lx-file',
                                index: 'fileManagement',
                                title: '文件管理'
                            },
                            {
                                index: 'multiFileParser',
                                title: '多文件解析',
                                icon: 'el-icon-lx-read'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-lx-copy',
                        index: '5',
                        title: '各项配置管理',
                        subs:[


                              {
                                icon: 'el-icon-lx-copy',
                                index: 'file_type',
                                title: '文件分类管理'
                            },
                            {
                                index: 'parser',
                                title: '解析器管理',
                                icon: 'el-icon-printer'
                            },
                            {
                                index: 'prinSuboRelation',
                                title: '主从关系',
                                icon: 'el-icon-lx-settings'
                            },
                            {
                                index: 'tableSet',
                                title: '表格映射',
                                icon: 'el-icon-setting'
                            },
                            {
                                index: 'kafkaHbaseConf',
                                title: '属性配置',
                                icon: 'el-icon-setting'
                            },
                            {
                                index: 'template',
                                title: '映射模板',
                                icon: 'el-icon-setting'
                            },
                        ]
                    },
                  /*  {
                        icon: 'el-icon-lx-copy',
                        index: '5',
                        title: '业务管理',
                        subs:[

                        ]
                    },*/


                    //{
                    //  icon: 'el-icon-lx-home',
                    //   index: 'block_manage',
                    //  title: '黑白名单管理'
                    // },
                    /*{
                        icon: 'el-icon-lx-cascades',
                        index: 'table',
                        title: '基础表格'

                    {
                        icon: 'el-icon-lx-copy',
                        index: 'tabs',
                        title: 'tab选项卡'
                    },
                    {
                        icon: 'el-icon-lx-calendar',
                        index: '3',
                        title: '表单相关',
                        subs: [
                            {
                                index: 'form',
                                title: '基本表单'
                            },
                            {
                                index: '3-2',
                                title: '三级菜单',
                                subs: [
                                    {
                                        index: 'editor',
                                        title: '富文本编辑器'
                                    },
                                    {
                                        index: 'markdown',
                                        title: 'markdown编辑器'
                                    },
                                ]
                            },
                            {
                                index: 'upload',
                                title: '文件上传'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-lx-emoji',
                        index: 'icon',
                        title: '自定义图标'
                    },*/
                    /*{
                        icon: 'el-icon-lx-favor',
                        index: 'charts',
                        title: 'schart图表'
                    },
                    {
                        icon: 'el-icon-rank',
                        index: 'drag',
                        title: '拖拽列表'
                    },
                    {
                        index: 'uploader',
                        title: '测试上传',
                        icon: 'el-icon-upload'
                    },
                    {
                        index: 'doLayout',
                        title: '测试布局',
                        icon: 'el-icon-lx-news'
                    },*/

                    /*    {
                            index: 'columeSet',
                            title: '字段映射',
                            icon: 'el-icon-setting'
                        },*/

                    /*,
                    {
                        icon: 'el-icon-lx-warn',
                        index: '6',
                        title: '错误处理',
                        subs: [
                            {
                                index: 'permission',
                                title: '权限测试'
                            },
                            {
                                index: '404',
                                title: '404页面'
                            }
                        ]
                    }*/
                ]
            }
        },
        computed:{
            onRoutes(){
                return this.$route.path.replace('/','');
            }
        },
        created(){
            // 通过 Event Bus 进行组件间通信，来折叠侧边栏
            bus.$on('collapse', msg => {
                this.collapse = msg;
        })
        }
    }
</script>
<style scoped>
    .sidebar{
        display: block;
        position: absolute;
        left: 0;
        top: 70px;
        bottom:0;
        overflow-y: scroll;
    }
    .sidebar::-webkit-scrollbar{
        width: 0;
    }
    .sidebar-el-menu:not(.el-menu--collapse){
        width: 250px;
    }
    .sidebar > ul {
        height:100%;
    }
</style>
