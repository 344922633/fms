import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            redirect: '/dashboard'
        },
        {
            path: '/',
            component: resolve => require(['../components/common/Home.vue'], resolve),
            meta: { title: '自述文件' },
            children:[
                {
                    path: '/dashboard',
                    component: resolve => require(['../components/page/Dashboard.vue'], resolve),
                    meta: { title: '系统首页' }
                },
                {
                    path: '/file_type',
                    component: resolve => require(['../components/page/fileType.vue'], resolve),
                    meta: { title: '文件分类管理' }
                },
                {
                    path: '/icon',
                    component: resolve => require(['../components/page/BlockManage.vue'], resolve),
                    meta: { title: '自定义图标' }
                },
                {
                    path: '/block_manage',
                        component: resolve => require(['../components/page/BlockManage.vue'], resolve),
                    meta: { title: '黑白名单管理' }
                },
                {
                    path: '/file_type',
                    component: resolve => require(['../components/page/demo/Icon.vue'], resolve),
                    meta: { title: '自定义图标' }
                },
                {
                    path: '/table',
                    component: resolve => require(['../components/page/demo/BaseTable.vue'], resolve),
                    meta: { title: '基础表格' }
                },
                {
                    path: '/tabs',
                    component: resolve => require(['../components/page/demo/Tabs.vue'], resolve),
                    meta: { title: 'tab选项卡' }
                },
                {
                    path: '/form',
                    component: resolve => require(['../components/page/demo/BaseForm.vue'], resolve),
                    meta: { title: '基本表单' }
                },
                {
                    // 富文本编辑器组件
                    path: '/editor',
                    component: resolve => require(['../components/page/demo/VueEditor.vue'], resolve),
                    meta: { title: '富文本编辑器' }
                },
                {
                    // markdown组件
                    path: '/markdown',
                    component: resolve => require(['../components/page/demo/Markdown.vue'], resolve),
                    meta: { title: 'markdown编辑器' }
                },
                {
                    path: '/fileManagement',
                    component: resolve => require(['../components/page/FileManagement.vue'], resolve),
                    meta: { title: '文件管理' }
                },
                {
                    // 图片上传组件
                    path: '/upload',
                    component: resolve => require(['../components/page/demo/Upload.vue'], resolve),
                    meta: { title: '文件上传' }
                },
                {
                    // vue-schart组件
                    path: '/charts',
                    component: resolve => require(['../components/page/demo/BaseCharts.vue'], resolve),
                    meta: { title: 'schart图表' }
                },
                {
                    // 拖拽列表组件
                    path: '/drag',
                    component: resolve => require(['../components/page/demo/DragList.vue'], resolve),
                    meta: { title: '拖拽列表' }
                },
                {
                    // 权限页面
                    path: '/permission',
                    component: resolve => require(['../components/page/demo/Permission.vue'], resolve),
                    meta: { title: '权限测试', permission: true }
                },
                {
                    // 上传页面
                    path: '/uploader',
                    component: resolve => require(['../components/page/vue-uploader/NewUploader.vue'], resolve),
                    meta: { title: '上传测试', permission: true }
                },
                {
                    path: '/multiFileParser',
                    component: resolve => require(['../components/page/fileparser/multiFileParser.vue'], resolve),
                    meta: { title: '多文件解析' }
                },
                {
                    path: '/prinSuboRelation',
                        component: resolve => require(['../components/page/fileparser/prinSuboRelation.vue'], resolve),
                    meta: { title: '主从关系管理' }
                },
                {
                    path: '/manualDataDeal',
                    component: resolve => require(['../components/page/manualdatadeal/ManualDataDeal.vue'], resolve),
                    meta: { title: '单文件解析' }
                },
                {
                    path: '/tuopu',
                    component: resolve => require(['../components/page/tuopu/Tuopu.vue'], resolve),
                    meta: { title: '拓扑图' }
                },

                {
                    path: '/tuopuManage',
                    component: resolve => require(['../components/page/tuopu/TuopuManage.vue'], resolve),
                    meta: { title: '拓扑图管理' }
                },
                {
                    path: '/control',
                    component: resolve => require(['../components/page/tuopu/Control.vue'], resolve),
                    meta: { title: '控件管理' }
                },
                {
                    path: '/fileInput',
                    component: resolve => require(['../components/page/fileInput.vue'], resolve),
                    meta: { title: '离线文件导入' }
                },
                {
                    //布局测试
                    path: '/doLayout',
                    component: resolve => require(['../components/page/demo/Layout.vue'], resolve),
                    meta: { title: '布局', permission: true }
                },
                {
                    path: '/parser',
                    component: resolve => require(['../components/page/fileparser/FileParserManage.vue'], resolve),
                    meta: { title: '解析器', permission: true}
                },
                {
                    path: '/404',
                    component: resolve => require(['../components/page/404.vue'], resolve),
                    meta: { title: '404' }
                },
                {
                    path: '/403',
                    component: resolve => require(['../components/page/403.vue'], resolve),
                    meta: { title: '403' }
                }
            ]
        },
        {
            path: '/login',
            component: resolve => require(['../components/page/Login.vue'], resolve)
        },
        {
            path: '*',
            redirect: '/404'
        }
    ]
})
