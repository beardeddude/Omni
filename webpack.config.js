const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const HtmlWebpackPlugin = require('html-webpack-plugin');
const path = require('path');
const ROOT = path.resolve(__dirname, 'src/main/webapp');
const DEST = path.resolve(__dirname, 'build/fe');

module.exports = {
  mode: 'development',
  entry: [
    path.resolve(ROOT, 'js/app.js'),
    path.resolve(ROOT, 'css/main.css')
  ],
  output: {
    // specificies the path for the output (index.html, base dir for *.js and *.css)
    path: DEST,
    filename: 'dist/bundle.js'
  },
  module: {
    rules: [{
      test: /\.scss$/,
      use: [
        MiniCssExtractPlugin.loader,
        "css-loader",
        "sass-loader"
      ]
    }]
  },
  plugins: [
    // Extracts CSS into a dedicated css files
    new MiniCssExtractPlugin({
      filename: "dist/[name].css",
      chunkFilename: "dist/[id].css"
    }),
    // Adds scripts & css to index.html
    new HtmlWebpackPlugin({
      template: '!!html-loader!' + path.resolve(ROOT, 'index.html')
    })
  ]
};
