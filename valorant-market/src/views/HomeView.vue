<template>
  <div class="main-content">

    <!-- 1. 筛选面板 -->
    <div class="filter-panel">
      <!-- 一级分类 -->
      <div class="filter-row">
        <div class="label">分类：</div>
        <div class="options">
          <div
            v-for="cat in mainCategories"
            :key="cat.code"
            class="tag"
            :class="{ active: currentMainCat === cat.code }"
            @click="handleMainClick(cat.code)"
          >
            {{ cat.name }}
          </div>
        </div>
      </div>

      <!-- 二级分类 -->
      <div class="filter-row sub-row" v-if="activeSubCats.length > 0">
        <div class="label">型号：</div>
        <div class="options">
          <div class="sub-tag" :class="{ active: currentWeapon === '' }" @click="handleSubClick('')">全部</div>
          <div
            v-for="sub in activeSubCats"
            :key="sub.code"
            class="sub-tag"
            :class="{ active: currentWeapon === sub.code }"
            @click="handleSubClick(sub.code)"
          >
            {{ sub.name }}
          </div>
        </div>
      </div>

      <!-- 搜索与统计 -->
      <div class="tool-row">
        <div class="total-info">共找到 <span class="highlight">{{ total }}</span> 件饰品</div>
        <div class="search-box">
           <el-input v-model="searchInput" placeholder="搜索..." class="dark-input" @keyup.enter="handleSearch">
             <template #append><el-button @click="handleSearch">搜索</el-button></template>
           </el-input>
        </div>
      </div>
    </div>

    <!-- 2. 商品列表 -->
    <div class="market-list" v-loading="loading" element-loading-background="rgba(0,0,0,0.8)">
      <div class="card-grid" v-if="skinList.length > 0">
        <div
          class="buff-card"
          v-for="skin in skinList"
          :key="skin.uuid"
          @click="goToDetail(skin)"
        >
          <div class="img-wrapper"><img :src="skin.displayIcon" loading="lazy"></div>
          <div class="info-wrapper">
            <div class="name" :title="skin.displayName">{{ skin.displayName }}</div>
            <div class="price">参考价 ¥{{ getFakePrice(skin.uuid) }}</div>
            <div class="meta">{{ getFakeCount(skin.uuid) }}件在售</div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无相关饰品" />
    </div>

    <!-- 3. 分页条 -->
    <div class="pagination-section">
      <el-pagination
        v-if="total > 0"
        background
        layout="prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="handlePageChange"
      />
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

// --- 状态数据 ---
const skinList = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const searchInput = ref('')
const currentMainCat = ref('')
const currentWeapon = ref('')

// --- 配置数据 ---
const mainCategories = [
  { name: '全部', code: '' }, { name: '步枪', code: 'rifle' }, { name: '手枪', code: 'pistol' },
  { name: '狙击', code: 'sniper' }, { name: '冲锋', code: 'smg' }, { name: '霰弹', code: 'shotgun' },
  { name: '机枪', code: 'machinegun' }, { name: '近战', code: 'melee' }
]
const subMap: any = {
  'rifle': [{ name: '狂徒', code: 'vandal' }, { name: '幻影', code: 'phantom' }, { name: '戍卫', code: 'guardian' }, { name: '獠犬', code: 'bulldog' }],
  'pistol': [{ name: '鬼魅', code: 'ghost' }, { name: '正义', code: 'sheriff' }, { name: '标配', code: 'classic' }, { name: '短炮', code: 'shorty' }, { name: '狂怒', code: 'frenzy' }],
  'sniper': [{ name: '冥狙', code: 'operator' }, { name: '飞将', code: 'marshal' }, { name: '莽侠', code: 'outlaw' }],
  'smg': [{ name: '骇灵', code: 'spectre' }, { name: '蜂刺', code: 'stinger' }],
  'shotgun': [{ name: '判官', code: 'judge' }, { name: '雄鹿', code: 'bucky' }],
  'machinegun': [{ name: '奥丁', code: 'odin' }, { name: '战神', code: 'ares' }]
}
const activeSubCats = computed(() => subMap[currentMainCat.value] || [])

// --- 核心方法 ---

// 跳转详情页
const goToDetail = (skin: any) => {
  // 使用 Vue Router 跳转，不再弹窗
  router.push(`/skin/${skin.uuid}`)
}

const handleMainClick = (code: string) => {
  currentMainCat.value = code
  currentWeapon.value = ''
  currentPage.value = 1
  loadData()
}

const handleSubClick = (code: string) => {
  currentWeapon.value = code
  currentPage.value = 1
  loadData()
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handlePageChange = (val: number) => {
  currentPage.value = val
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/skins/page', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        category: currentMainCat.value,
        weapon: currentWeapon.value,
        search: searchInput.value
      }
    })
    skinList.value = res.data.records
    total.value = res.data.total
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } catch(e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 模拟价格计算
const getFakePrice = (uuid: string) => {
  let h=0;for(let i=0;i<uuid.length;i++)h=uuid.charCodeAt(i)+((h<<5)-h);
  return(Math.abs(h%4000)+50).toFixed(2);
}
const getFakeCount = (uuid: string) => (uuid.charCodeAt(0)%50)+1

onMounted(() => loadData())
</script>

<style scoped>
/* 核心布局 */
.main-content { margin-top: 20px; }

/* 筛选区 */
.filter-panel { background: #1c1f26; padding: 20px; border-radius: 4px; margin-bottom: 20px; border: 1px solid #2a2e38; }
.filter-row { display: flex; margin-bottom: 15px; }
.label { width: 50px; color: #666; font-size: 14px; line-height: 28px; flex-shrink: 0; }
.options { flex: 1; display: flex; flex-wrap: wrap; gap: 10px; }
.tag, .sub-tag { padding: 4px 12px; cursor: pointer; border-radius: 2px; color: #999; transition: 0.2s; }
.tag:hover, .tag.active, .sub-tag:hover, .sub-tag.active { color: #d6ad5e; background: #2a2e39; font-weight: bold; }
.tool-row { border-top: 1px solid #2a2e39; padding-top: 15px; display: flex; justify-content: space-between; align-items: center; }
.highlight { color: #d6ad5e; font-weight: bold; margin: 0 5px; }
.search-box { width: 250px; }
:deep(.dark-input .el-input__wrapper) { background: #15181e; border-color: #333; box-shadow: none; }
:deep(.dark-input .el-input__inner) { color: #fff; }

/* 商品列表 */
.market-list { min-height: 600px; }
.card-grid { display: grid; grid-template-columns: repeat(5, 230px); justify-content: space-between; row-gap: 20px; }
.buff-card { width: 230px; background: #1e222d; border: 1px solid #262933; cursor: pointer; transition: 0.2s; }
.buff-card:hover { border-color: #d6ad5e; transform: translateY(-4px); }
.img-wrapper { height: 130px; background: radial-gradient(#2a2e39, #1e222d); display: flex; justify-content: center; align-items: center; padding: 10px; }
.img-wrapper img { max-width: 90%; max-height: 90%; object-fit: contain; }
.info-wrapper { padding: 12px; background: #20242f; }
.name { font-size: 12px; color: #ccc; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-bottom: 6px; }
.price { font-size: 16px; color: #d6ad5e; font-weight: bold; }
.meta { font-size: 12px; color: #666; margin-top: 4px; }

/* 分页 */
.pagination-section { margin: 40px auto; display: flex; justify-content: center; padding: 20px; background: #1c1f26; border-radius: 8px; width: fit-content; }
:deep(.el-pagination.is-background .el-pager li) { background-color: #2a2e39; color: #ccc; border: 1px solid #444; }
:deep(.el-pagination.is-background .el-pager li.is-active) { background-color: #d6ad5e; color: black; border-color: #d6ad5e; font-weight: bold; }
:deep(.el-pagination.is-background .btn-prev), :deep(.el-pagination.is-background .btn-next) { background-color: #2a2e39; color: #ccc; }
</style>