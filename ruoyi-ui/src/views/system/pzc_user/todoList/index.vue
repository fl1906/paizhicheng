<template>

  <div class="app-container">
    <div>
      <h1>待办事项</h1>
    </div>
    <el-input v-model="input" placeholder="请输入需要填入的代办事项" @change="handleAdd(input)"></el-input>
    <hr>
    <el-table
      :data="tableData"
      style="width: 100%">
      <el-table-column
        label="编号"
        width="180">
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="事件名称"
        width="180">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="top">
            <p>事件名称: {{ scope.row.name }}</p>
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium">{{ scope.row.name }}</el-tag>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.$index, scope.row)">编辑
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>


</template>


<script>
export default {
  data() {
    return {
      input: '',
      tableData: [{
        id: 1,
        name: '完成代办列表',
      }
      ]
    }
  },
  methods: {
    handleEdit(index, row) {
      console.log(index, row);
    },
    handleDelete(index,row) {
      console.log(row);
      this.tableData = this.tableData.filter(item => item.id !== row.id); //过滤掉选中的那个值并且 重新赋值给Data
    },
    handleAdd(data) {
      let lastItemId =0
      if(this.tableData.length>0)
      {
        lastItemId = this.tableData[this.tableData.length - 1].id;
      }
      // Create a new item object with input as name and last item's ID + 1
      const newItem = {
        id: lastItemId + 1,
        name: this.input
      };
      console.log("当前用户输入的代办事项为: ", this.input);

      // Add the new item to the tableData array
      this.tableData.push(newItem);
      this.input=''
    }
  }
}
</script>

<style scoped>

</style>
