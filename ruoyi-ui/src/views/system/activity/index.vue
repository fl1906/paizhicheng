<template>
  <div class="home_div">
    <div class="map_title">
      <h3>JSAPI Vue2地图组件示例</h3>
    </div>
    <div id="container"></div>
    <div id="panel" style='position: absolute; right: 10px;top: 10px;height: 480px;width: 300px;z-index: 100;overflow: auto;'></div>
  </div>
</template>
<script>
import AMapLoader from '@amap/amap-jsapi-loader';

export default {
  name: "Mapview",
  data() {
    return {
      map:null,
    }
  },
  created() {

  },
  mounted() {
    this.initAMap();
  },
  methods: {

    initAMap() {
      AMapLoader.load({
        key: 'e739fb53aaafb9514f058d8f39108519',  //设置您的key
        version: "2.0", //指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        plugins: ['AMap.ToolBar', 'AMap.Driving'],//需要使用的的插件列表，如比例尺'AMap.Scale'等
        AMapUI: {
          version: "1.1",
          plugins: [],

        },
        Loca: {
          version: "2.0"  //数据可视化
        },
      }).then((AMap) => {
        this.map = new AMap.Map("container", {
          viewMode: '2D',  // 默认使用 2D 模式
          zoom:5,  //初始化地图层级
          center: [116.397428, 39.90923]  //初始化地图中心点
        });

        this.driving = new AMap.Driving({
          // 驾车路线规划策略，AMap.DrivingPolicy.LEAST_TIME是最快捷模式
          policy: AMap.DrivingPolicy.LEAST_TIME,
          // map 指定将路线规划方案绘制到对应的AMap.Map对象上
          map: this.map,
          // panel 指定将结构化的路线详情数据显示的对应的DOM上，传入值需是DOM的ID
          panel: 'panel'
        })

        this.points = [
          { keyword: '北京市地震局（公交站）',city:'北京' },
          { keyword: '陆家嘴',city:'上海' }
        ]

        this.driving.search(this.points, function (status, result) {
          console.log(status, result);
        })

      }).catch(e => {
        console.log(e);
      })
    },
  }


}
</script>
<style  scoped>
.home_div {
  padding: 0px;
  margin: 0px;
  width: 100%;
  height: 100%;
  position: relative;
}

#container {
  padding: 0px;
  margin: 0px;
  width: 100%;
  height: 100%;
  position: absolute;
}

.map_title {
  position: absolute;
  z-index: 1;
  width: 100%;
  height: 50px;
  background-color: rgba(27, 25, 27, 0.884);

}

h3 {
  position: absolute;
  left: 10px;
  z-index: 2;
  color: white;
}
</style>
