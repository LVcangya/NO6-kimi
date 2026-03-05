<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
					<div class="search_view">
						<div class="search_label">
							医生账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.yishengzhanghao" placeholder="医生账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.zhanghao" placeholder="账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							发送状态：
						</div>
						<div class="search_box">
							<el-select
								class="search_sel"
								clearable
								v-model="searchQuery.sendstatus"
								placeholder="发送状态"
								>
								<el-option label="待发送" :value="0"></el-option>
								<el-option label="已发送" :value="1"></el-option>
								<el-option label="发送失败" :value="2"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button class="add_btn" type="success" @click="addClick" v-if="btnAuth('jiuzhentongzhi','新增')">
						<i class="iconfont icon-xinzeng1"></i>
						新增
					</el-button>
					<el-button class="del_btn" type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('jiuzhentongzhi','删除')">
						<i class="iconfont icon-shanchu4"></i>
						删除
					</el-button>
					<el-button class="retry_btn" type="warning" :disabled="selRows.length?false:true" @click="batchRetryClick" v-if="btnAuth('jiuzhentongzhi','重试')">
						<i class="iconfont icon-refresh"></i>
						批量重试
					</el-button>
					<el-button class="failed_btn" type="info" @click="showFailedList" v-if="btnAuth('jiuzhentongzhi','查看失败')">
						<i class="iconfont icon-error"></i>
						查看失败记录
					</el-button>
				</div>
			</div>
			<el-table
				v-loading="listLoading"
				border
				:stripe='false'
				@selection-change="handleSelectionChange"
				ref="table"
				v-if="btnAuth('jiuzhentongzhi','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' align="left" header-align="left">
					<template #default="scope">{{ (listQuery.page-1)*listQuery.limit+scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhibianhao"
					label="通知编号">
					<template #default="scope">
						{{scope.row.tongzhibianhao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="yishengzhanghao"
					label="医生账号">
					<template #default="scope">
						{{scope.row.yishengzhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="dianhua"
					label="电话">
					<template #default="scope">
						{{scope.row.dianhua}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jiuzhenshijian"
					label="就诊时间">
					<template #default="scope">
						{{scope.row.jiuzhenshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhishijian"
					label="通知时间">
					<template #default="scope">
						{{scope.row.tongzhishijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="zhanghao"
					label="账号">
					<template #default="scope">
						{{scope.row.zhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shouji"
					label="手机">
					<template #default="scope">
						{{scope.row.shouji}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhibeizhu"
					label="通知备注">
					<template #default="scope">
						{{scope.row.tongzhibeizhu}}
					</template>
				</el-table-column>
				<el-table-column min-width="120"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="sendstatus"
					label="发送状态">
					<template #default="scope">
						<el-tag v-if="scope.row.sendstatus == 0" type="warning">待发送</el-tag>
						<el-tag v-else-if="scope.row.sendstatus == 1" type="success">已发送</el-tag>
						<el-tag v-else-if="scope.row.sendstatus == 2" type="danger">发送失败</el-tag>
						<el-tag v-else type="info">未知</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="retrycount"
					label="重试次数">
					<template #default="scope">
						{{scope.row.retrycount || 0}}
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="failreason"
					label="失败原因">
					<template #default="scope">
						<span v-if="scope.row.failreason" style="color: #f56c6c;">{{scope.row.failreason}}</span>
						<span v-else>-</span>
					</template>
				</el-table-column>
				<el-table-column min-width="160"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="lastsendtime"
					label="最后发送时间">
					<template #default="scope">
						{{scope.row.lastsendtime}}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="350" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button class="view_btn" type="info" v-if=" btnAuth('jiuzhentongzhi','查看')" @click="infoClick(scope.row.id)">
							<i class="iconfont icon-sousuo2"></i>
							查看
						</el-button>
						<el-button class="edit_btn" type="primary" @click="editClick(scope.row.id)" v-if=" btnAuth('jiuzhentongzhi','修改')">
							<i class="iconfont icon-xiugai5"></i>
							修改
						</el-button>
						<el-button class="del_btn" type="danger" @click="delClick(scope.row.id)"  v-if="btnAuth('jiuzhentongzhi','删除')">
							<i class="iconfont icon-shanchu4"></i>
							删除
						</el-button>
						<el-button class="retry_btn" type="warning" v-if="scope.row.sendstatus == 2 && btnAuth('jiuzhentongzhi','重试')" @click="retryClick(scope.row.id)">
							<i class="iconfont icon-refresh"></i>
							重试
						</el-button>
						<el-button class="send_btn" type="success" v-if="scope.row.sendstatus != 1 && btnAuth('jiuzhentongzhi','发送')" @click="sendClick(scope.row.id)">
							<i class="iconfont icon-send"></i>
							发送
						</el-button>
						<el-button class="cross_btn" v-if="btnAuth('jiuzhentongzhi','签到')" type="success" @click="jiuzhenqiandaoCrossAddOrUpdateHandler(scope.row,'cross','','','','')">
							<i class="iconfont icon-dingdan3"></i>
							签到
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total"
				:page-size="listQuery.limit"
                v-model:current-page="listQuery.page"
				prev-text="上一页"
				next-text="下一页"
				:hide-on-single-page="false"
				:style='{}'
				:page-sizes="[10, 20, 30, 40, 50, 100]"
				@size-change="sizeChange"
				@current-change="currentChange"  />
		</div>
		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
		<jiuzhenqiandaoFormModel ref="jiuzhenqiandaoFormModelRef" @formModelChange="formModelChange"></jiuzhenqiandaoFormModel>

		<!-- 失败记录弹窗 -->
		<el-dialog v-model="failedDialogVisible" title="发送失败的通知记录" width="80%">
			<el-table :data="failedList" border v-loading="failedLoading">
				<el-table-column prop="tongzhibianhao" label="通知编号" min-width="140"></el-table-column>
				<el-table-column prop="yishengzhanghao" label="医生账号" min-width="140"></el-table-column>
				<el-table-column prop="zhanghao" label="用户账号" min-width="140"></el-table-column>
				<el-table-column prop="shouji" label="手机" min-width="140"></el-table-column>
				<el-table-column prop="retrycount" label="重试次数" min-width="100"></el-table-column>
				<el-table-column prop="failreason" label="失败原因" min-width="200">
					<template #default="scope">
						<span style="color: #f56c6c;">{{scope.row.failreason}}</span>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="200">
					<template #default="scope">
						<el-button type="warning" size="small" @click="retryClick(scope.row.id)">重试</el-button>
						<el-button type="primary" size="small" @click="sendClick(scope.row.id)">立即发送</el-button>
					</template>
				</el-table-column>
			</el-table>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="failedDialogVisible = false">关闭</el-button>
					<el-button type="primary" @click="retryAllFailed">全部重试</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
	import axios from 'axios'
    import moment from "moment"
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
		computed,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox
	} from 'element-plus'
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import formModel from './formModel.vue'
	//基础信息

	const tableName = 'jiuzhentongzhi'
	const formName = '就诊通知'
	const route = useRoute()
	//基础信息
	onMounted(()=>{
	})
	//列表数据
	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 10,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			//table.value.clearSelection()
			table.value.toggleRowSelection(row)
		})
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.yishengzhanghao&&searchQuery.value.yishengzhanghao!=''){
			params['yishengzhanghao'] = '%' + searchQuery.value.yishengzhanghao + '%'
		}
		if(searchQuery.value.zhanghao&&searchQuery.value.zhanghao!=''){
			params['zhanghao'] = '%' + searchQuery.value.zhanghao + '%'
		}
		if(searchQuery.value.sendstatus !== undefined && searchQuery.value.sendstatus !== ''){
			params['sendstatus'] = searchQuery.value.sendstatus
		}
		context.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}
	//删
	const delClick = (id) => {
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids.value
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
				})
			})
		}).catch(_ => {})
	}
	//多选
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
	//列表数据
	//分页
	const total = ref(0)
	const layouts = ref(["total","prev","pager","next","sizes","jumper"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}
	//分页
	//权限验证
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	//搜索
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	//表单
	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}
	const addClick = ()=>{
		formRef.value.init()
	}
	const editClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'edit')
			return
		}
		if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'edit')
		}
	}

	const infoClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'info')
		}
		else if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'info')
		}
	}
	// 表单
	// 预览文件
	const preClick = (file) =>{
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		window.open(context?.$config.url + file)
	}
	// 下载文件
	const download = (file) => {
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		let arr = file.replace(new RegExp('file/', "g"), "")
		axios.get((location.href.split(context?.$config.name).length>1 ? location.href.split(context?.$config.name)[0] :'') + context?.$config.name + '/file/download?fileName=' + arr, {
			headers: {
				token: context?.$toolUtil.storageGet('Token')
			},
			responseType: "blob"
		}).then(({
			data
		}) => {
			const binaryData = [];
			binaryData.push(data);
			const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
				type: 'application/pdf;chartset=UTF-8'
			}))
			const a = document.createElement('a')
			a.href = objectUrl
			a.download = arr
			// a.click()
			// 下面这个写法兼容火狐
			a.dispatchEvent(new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
				view: window
			}))
			window.URL.revokeObjectURL(data)
		})
	}
	import jiuzhenqiandaoFormModel from '@/views/jiuzhenqiandao/formModel'
	const jiuzhenqiandaoFormModelRef = ref(null)
    const jiuzhenqiandaoCrossAddOrUpdateHandler = (row,type,crossOptAudit,crossOptPay,statusColumnName,tips,statusColumnValue) => {
		if(statusColumnName!=''&&!statusColumnName.startsWith("[")) {
			var obj = row
			for (var o in obj){
				if(o==statusColumnName && obj[o]==statusColumnValue){
					context?.$toolUtil.message(tips,'error')
					return;
				}
			}
		}
		nextTick(()=>{
			jiuzhenqiandaoFormModelRef.value.init(row.id,'cross','签到',row,'jiuzhentongzhi',statusColumnName,tips,statusColumnValue)
		})
    }

	// 重试发送通知
	const retryClick = (id) => {
		ElMessageBox.confirm('确定要重试发送该通知吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/retry/${id}`,
				method: 'get'
			}).then(res => {
				if(res.data.code == 0) {
					context?.$toolUtil.message(res.data.msg || '重试发送成功', 'success')
					getList()
					if(failedDialogVisible.value) {
						getFailedList()
					}
				} else {
					context?.$toolUtil.message(res.data.msg || '重试发送失败', 'error')
				}
			})
		}).catch(() => {})
	}

	// 批量重试
	const batchRetryClick = () => {
		if(!selRows.value.length) {
			context?.$toolUtil.message('请选择要重试的记录', 'error')
			return
		}
		const ids = selRows.value.map(row => row.id)
		ElMessageBox.confirm(`确定要批量重试选中的 ${ids.length} 条通知吗？`, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/retryBatch`,
				method: 'post',
				data: ids
			}).then(res => {
				if(res.data.code == 0) {
					context?.$toolUtil.message(res.data.msg || '批量重试成功', 'success')
					getList()
				} else {
					context?.$toolUtil.message(res.data.msg || '批量重试失败', 'error')
				}
			})
		}).catch(() => {})
	}

	// 手动发送通知
	const sendClick = (id) => {
		ElMessageBox.confirm('确定要立即发送该通知吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/send/${id}`,
				method: 'get'
			}).then(res => {
				if(res.data.code == 0) {
					context?.$toolUtil.message(res.data.msg || '发送成功', 'success')
					getList()
					if(failedDialogVisible.value) {
						getFailedList()
					}
				} else {
					context?.$toolUtil.message(res.data.msg || '发送失败', 'error')
				}
			})
		}).catch(() => {})
	}

	// 失败记录相关
	const failedDialogVisible = ref(false)
	const failedList = ref([])
	const failedLoading = ref(false)

	// 查看失败记录
	const showFailedList = () => {
		failedDialogVisible.value = true
		getFailedList()
	}

	// 获取失败列表
	const getFailedList = () => {
		failedLoading.value = true
		context.$http({
			url: `${tableName}/failedList`,
			method: 'get'
		}).then(res => {
			failedLoading.value = false
			if(res.data.code == 0) {
				failedList.value = res.data.data
			}
		}).catch(() => {
			failedLoading.value = false
		})
	}

	// 重试所有失败记录
	const retryAllFailed = () => {
		if(!failedList.value.length) {
			context?.$toolUtil.message('没有失败记录需要重试', 'info')
			return
		}
		const ids = failedList.value.map(item => item.id)
		ElMessageBox.confirm(`确定要重试所有 ${ids.length} 条失败记录吗？`, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/retryBatch`,
				method: 'post',
				data: ids
			}).then(res => {
				if(res.data.code == 0) {
					context?.$toolUtil.message(res.data.msg || '批量重试成功', 'success')
					getFailedList()
					getList()
				} else {
					context?.$toolUtil.message(res.data.msg || '批量重试失败', 'error')
				}
			})
		}).catch(() => {})
	}

	//初始化
	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>

	// 操作盒子
	.list_search_view {
		// 搜索盒子
		.search_form {
			// 子盒子
			.search_view {
				// 搜索label
				.search_label {
				}
				// 搜索item
				.search_box {
					// 输入框
					:deep(.search_inp) {
					}
				}
			}
			// 搜索按钮盒子
			.search_btn_view {
				// 搜索按钮
				.search_btn {
				}
				// 搜索按钮-悬浮
				.search_btn:hover {
				}
			}
		}
		//头部按钮盒子
		.btn_view {
			// 其他
			:deep(.el-button--default){
			}
			// 其他-悬浮
			:deep(.el-button--default:hover){
			}
			// 新增
			:deep(.el-button--success){
			}
			// 新增-悬浮
			:deep(.el-button--success:hover){
			}
			// 删除
			:deep(.el-button--danger){
			}
			// 删除-悬浮
			:deep(.el-button--danger:hover){
			}
			// 统计
			:deep(.el-button--warning){
			}
			// 统计-悬浮
			:deep(.el-button--warning:hover){
			}
		}
	}
	// 表格样式
	.el-table {
		:deep(.el-table__header-wrapper) {
			thead {
				tr {
					th {
						.cell {
						}
					}
				}
			}
		}
		:deep(.el-table__body-wrapper) {
			tbody {
				tr {
					td {
						.cell {
							// 编辑
							.el-button--primary {
							}
							// 编辑-悬浮
							.el-button--primary:hover {
							}
							// 详情
							.el-button--info {
							}
							// 详情-悬浮
							.el-button--info:hover {
							}
							// 删除
							.el-button--danger {
							}
							// 删除-悬浮
							.el-button--danger:hover {
							}
							// 跨表
							.el-button--success {
							}
							// 跨表-悬浮
							.el-button--success:hover {
							}
							// 操作
							.el-button--warning {
							}
							// 操作-悬浮
							.el-button--warning:hover {
							}
						}
					}
				}
			}
		}
	}
	// 操作栏样式
	.el-table-column--operate {
		.cell {
			// 按钮间距
			.el-button {
				margin: 0 4px;
			}
		}
	}
</style>