<template>
	<div>
		<el-dialog class="edit_form" v-model="approvalVisible" :title="'审核'" width="70%" destroy-on-close>
			<el-form ref="ruleFormRef" :model="approvalForm" label-width="120px" :rules="rules">
				<el-row>
					<el-col :span="24">
						<el-form-item label="审核回复" prop="shhf">
							<el-input v-model="approvalForm.shhf" type="textarea" />
						</el-form-item>
					</el-col>

				</el-row>
			</el-form>
			<template #footer>
				<span class="dialog-footer" v-if="sfshType==1">
					<el-button type="danger" @click="approvalSave('否')">拒绝</el-button>
					<el-button type="primary" @click="approvalSave('是')">
						通过
					</el-button>
				</span>
				<span class="dialog-footer" v-if="sfshType==2">
					<el-button type="danger" @click="approvalVisible=false">取消</el-button>
					<el-button type="primary" @click="approvalSave()">
						回复
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import {
		ref,
		toRefs,
		getCurrentInstance,
		defineEmits
	} from 'vue';
	const emit = defineEmits(['shChange'])
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	//props
	const props = defineProps({
		tableName: String
	})
	const {
		tableName
	} = toRefs(props)
	//props

	const rules = ref({
		shhf: [{
			required: true,
			message: '请输入审核回复',
			trigger: 'blur'
		}, ],
	})
	const approvalForm = ref({})
	const approvalVisible = ref(false)
	//ref
	const ruleFormRef = ref(null)
	const sfshType = ref(1)

	const approvalClick = (row,type=1) => {
		sfshType.value = type
		approvalForm.value = JSON.parse(JSON.stringify(row))
		approvalVisible.value = true
	}

	//声明父级调用
	defineExpose({
		approvalClick
	})
	const approvalSave = (type) => {
		if(sfshType.value == 1){
			approvalForm.value.sfsh = type
		}
		ruleFormRef.value.validate((valid) => {
			if (valid) {
				// 使用批量审核接口，支持通知自动发送
				let url = `${tableName.value}/shBatch`
				context?.$http({
					url: url,
					method: 'post',
					data: [approvalForm.value.id],
					params: {
						sfsh: approvalForm.value.sfsh,
						shhf: approvalForm.value.shhf
					}
				}).then(res => {
					let msg = '审核成功'
					// 如果有通知发送结果，显示在消息中
					if(res.data.notificationMsg && res.data.notificationMsg.length > 0) {
						msg += '\n' + res.data.notificationMsg.join('\n')
					}
					context?.$toolUtil.message(msg, 'success', obj => {
						approvalVisible.value = false
					})
					emit('shChange',type,approvalForm.value)
				})
			}
		})
	}
</script>

<style>
</style>
