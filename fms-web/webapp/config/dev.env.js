'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  BASE_UPLOAD: '"http://localhost:8088/fms/"',
  BASE_URL: '""'
})
