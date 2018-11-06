import Vue from 'vue';
import Vuex from 'vuex';
import permission from './permission';
import user from './user';
import getters from './getters';

Vue.use(Vuex);

const store = new Vuex.Store({
    modules: {
        permission,
        user
    },
    getters
})

export default store
