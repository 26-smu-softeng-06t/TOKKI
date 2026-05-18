import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { StageService } from '../services/StageService';
import type { DifficultyLevel, ExcelUploadPreview, Stage, StageInput } from '../types';

const SEED_DATA: StageInput[] = (['easy', 'medium', 'hard'] as DifficultyLevel[]).flatMap(
  (difficulty) =>
    Array.from({ length: 10 }, (_, si) => ({
      difficulty,
      stageNumber: si + 1,
      words: Array.from({ length: 10 }, (_, wi) => ({
        word: `word${wi + 1}`,
        meaning: `단어${wi + 1}`,
        example: null as null,
        orderIndex: wi + 1,
      })),
    }))
);

const EMPTY_WORDS = Array.from({ length: 10 }, (_, i) => ({
  word: '',
  meaning: '',
  example: null as null,
  orderIndex: i + 1,
}));

interface FormData {
  difficulty: DifficultyLevel;
  stageNumber: number;
  words: { word: string; meaning: string; example: null; orderIndex: number }[];
}

const INITIAL_FORM: FormData = {
  difficulty: 'easy',
  stageNumber: 1,
  words: EMPTY_WORDS,
};

const DIFFICULTY_BADGE: Record<DifficultyLevel, string> = {
  easy: 'bg-green-100 text-green-700',
  medium: 'bg-amber-100 text-amber-700',
  hard: 'bg-red-100 text-red-700',
};

export default function AdminPage() {
  const navigate = useNavigate();
  const [stages, setStages] = useState<Stage[]>([]);
  const [loading, setLoading] = useState(true);
  const [seeding, setSeeding] = useState(false);
  const [saving, setSaving] = useState(false);
  const [previewingUpload, setPreviewingUpload] = useState(false);
  const [uploadingExcel, setUploadingExcel] = useState(false);
  const [excelPreview, setExcelPreview] = useState<ExcelUploadPreview | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState<FormData>(INITIAL_FORM);

  useEffect(() => {
    fetchStages();
  }, []);

  async function fetchStages() {
    setLoading(true);
    try {
      const data = await StageService.getStages();
      setStages(data);
    } catch {
      toast.error('스테이지 목록을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }

  function resetForm() {
    setEditingId(null);
    setFormData(INITIAL_FORM);
  }

  function startEdit(stage: Stage) {
    setEditingId(stage.stageId);
    setFormData({
      difficulty: stage.difficulty,
      stageNumber: stage.stageNumber,
      words: Array.from({ length: 10 }, (_, i) => {
        const w = stage.words[i];
        return w
          ? { word: w.word, meaning: w.meaning, example: null, orderIndex: i + 1 }
          : { word: '', meaning: '', example: null, orderIndex: i + 1 };
      }),
    });
  }

  async function handleSeed() {
    setSeeding(true);
    try {
      await StageService.batchUploadStages(SEED_DATA);
      toast.success('Seed 완료!');
      fetchStages();
    } catch {
      toast.error('Seed 실패');
    } finally {
      setSeeding(false);
    }
  }

  async function handleSave() {
    setSaving(true);
    try {
      if (editingId) {
        await StageService.updateStage(editingId, formData);
        toast.success('스테이지가 수정되었습니다.');
      } else {
        await StageService.createStage(formData);
        toast.success('스테이지가 추가되었습니다.');
      }
      resetForm();
      fetchStages();
    } catch {
      toast.error('저장에 실패했습니다.');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(stageId: number) {
    if (!confirm('삭제하시겠습니까?')) return;
    try {
      await StageService.deleteStage(stageId);
      toast.success('삭제되었습니다.');
      fetchStages();
    } catch {
      toast.error('삭제에 실패했습니다.');
    }
  }

  async function handleExcelFile(file: File | null) {
    setExcelPreview(null);
    if (!file) return;
    if (!file.name.toLowerCase().endsWith('.xlsx')) {
      toast.error('.xlsx 파일만 업로드할 수 있습니다.');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      toast.error('5MB 이하 파일만 업로드할 수 있습니다.');
      return;
    }

    setPreviewingUpload(true);
    try {
      const preview = await StageService.previewExcelUpload(file);
      setExcelPreview(preview);
      if (preview.valid) {
        toast.success(`${preview.rowCount}개 행을 확인했습니다.`);
      } else {
        toast.error('엑셀 검증 오류를 확인해주세요.');
      }
    } catch {
      toast.error('엑셀 파일을 미리보기 할 수 없습니다.');
    } finally {
      setPreviewingUpload(false);
    }
  }

  async function handleConfirmExcelUpload() {
    if (!excelPreview?.valid || excelPreview.stages.length === 0) return;
    setUploadingExcel(true);
    try {
      await StageService.batchUploadStages(excelPreview.stages);
      toast.success('엑셀 데이터가 DB에 반영되었습니다.');
      setExcelPreview(null);
      fetchStages();
    } catch {
      toast.error('엑셀 데이터 반영에 실패했습니다.');
    } finally {
      setUploadingExcel(false);
    }
  }

  function setWord(index: number, field: 'word' | 'meaning', value: string) {
    setFormData((prev) => {
      const words = [...prev.words];
      words[index] = { ...words[index], [field]: value };
      return { ...prev, words };
    });
  }

  return (
    <div className="min-h-screen bg-slate-50 p-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <button
            onClick={() => navigate('/')}
            className="text-slate-600 hover:text-slate-900 font-medium"
          >
            ← Back to Home
          </button>
          <h1 className="text-3xl font-bold">Admin Panel</h1>
          <button
            onClick={handleSeed}
            disabled={seeding}
            className="bg-amber-500 hover:bg-amber-600 disabled:opacity-60 text-white rounded-lg px-4 py-2 font-medium"
          >
            {seeding ? 'Seeding...' : '🗄 Seed Data'}
          </button>
        </div>

        {/* Content Grid */}
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Left: Form */}
          <div className="sticky top-8 h-fit bg-white p-6 rounded-2xl shadow-sm border">
            {/* Form Header */}
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-lg font-bold">
                {editingId ? 'Edit Stage' : '[+] Add New Stage'}
              </h2>
              {editingId && (
                <button
                  onClick={resetForm}
                  className="text-sm text-slate-500 hover:text-slate-800"
                >
                  Cancel
                </button>
              )}
            </div>

            {/* Difficulty + Stage Number */}
            <div className="grid grid-cols-2 gap-3 mb-4">
              <div>
                <label className="text-xs font-semibold text-slate-500 uppercase mb-1 block">
                  Difficulty
                </label>
                <select
                  value={formData.difficulty}
                  onChange={(e) =>
                    setFormData((p) => ({ ...p, difficulty: e.target.value as DifficultyLevel }))
                  }
                  className="w-full bg-slate-50 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="easy">Easy</option>
                  <option value="medium">Medium</option>
                  <option value="hard">Hard</option>
                </select>
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-500 uppercase mb-1 block">
                  Stage No.
                </label>
                <input
                  type="number"
                  min={1}
                  max={10}
                  value={formData.stageNumber}
                  onChange={(e) =>
                    setFormData((p) => ({ ...p, stageNumber: Number(e.target.value) }))
                  }
                  className="w-full bg-slate-50 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
            </div>

            {/* Words */}
            <div className="mb-4">
              <label className="text-xs font-semibold text-slate-500 uppercase mb-2 block">
                Words (10 required)
              </label>
              <div className="max-h-[400px] overflow-y-auto space-y-2 pr-1">
                {formData.words.map((w, i) => (
                  <div key={i} className="grid grid-cols-2 gap-2">
                    <input
                      type="text"
                      placeholder={`Word ${i + 1}`}
                      value={w.word}
                      onChange={(e) => setWord(i, 'word', e.target.value)}
                      className="p-2 text-sm bg-slate-50 border rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-400"
                    />
                    <input
                      type="text"
                      placeholder="뜻"
                      value={w.meaning}
                      onChange={(e) => setWord(i, 'meaning', e.target.value)}
                      className="p-2 text-sm bg-slate-50 border rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-400"
                    />
                  </div>
                ))}
              </div>
            </div>

            {/* Save Button */}
            <button
              onClick={handleSave}
              disabled={saving}
              className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white rounded-xl shadow-lg font-semibold mb-6"
            >
              {saving ? 'Saving...' : '💾 Save Stage'}
            </button>

            {/* Bulk Upload */}
            <div>
              <h3 className="text-sm font-bold text-slate-700 mb-2">📂 Bulk Upload (Excel)</h3>
              <label className="block border-2 border-dashed border-slate-300 rounded-xl p-6 text-center cursor-pointer hover:border-indigo-300 hover:bg-indigo-50/40 transition-colors">
                <span className="block text-sm text-slate-500">
                  {previewingUpload ? 'Checking file...' : 'Choose .xlsx file for preview'}
                </span>
                <span className="block text-xs text-slate-400 mt-1">difficulty, stageNumber, orderIndex, word, meaning</span>
                <input
                  type="file"
                  accept=".xlsx"
                  disabled={previewingUpload}
                  className="hidden"
                  onChange={(e) => handleExcelFile(e.target.files?.[0] ?? null)}
                />
              </label>

              {excelPreview && (
                <div className="mt-3 rounded-xl border border-slate-200 bg-slate-50 p-3">
                  <div className="flex items-center justify-between text-xs font-semibold text-slate-600 mb-2">
                    <span>{excelPreview.rowCount} rows</span>
                    <span>{excelPreview.stages.length} stages</span>
                  </div>
                  {excelPreview.errors.length > 0 ? (
                    <div className="max-h-32 overflow-y-auto space-y-1">
                      {excelPreview.errors.map((error, index) => (
                        <p key={`${error.rowNumber}-${error.field}-${index}`} className="text-xs text-red-600">
                          Row {error.rowNumber} · {error.field}: {error.message}
                        </p>
                      ))}
                    </div>
                  ) : (
                    <div className="max-h-32 overflow-y-auto space-y-1">
                      {excelPreview.stages.slice(0, 5).map((stage) => (
                        <p key={`${stage.difficulty}-${stage.stageNumber}`} className="text-xs text-slate-600">
                          {stage.difficulty} Stage {stage.stageNumber}: {stage.words.length} words
                        </p>
                      ))}
                      {excelPreview.stages.length > 5 && (
                        <p className="text-xs text-slate-400">+ {excelPreview.stages.length - 5} more stages</p>
                      )}
                    </div>
                  )}
                </div>
              )}
              <button
                disabled={!excelPreview?.valid || uploadingExcel}
                onClick={handleConfirmExcelUpload}
                className="mt-2 w-full py-2 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-100 disabled:text-slate-400 disabled:cursor-not-allowed text-white rounded-lg text-sm font-medium"
              >
                {uploadingExcel ? 'Uploading...' : 'Upload to DB'}
              </button>
            </div>
          </div>

          {/* Right: Stage List */}
          <div className="lg:col-span-2">
            <h2 className="text-2xl font-bold mb-4">Existing Stages</h2>
            {loading ? (
              <div className="p-12 text-center text-slate-400">Loading...</div>
            ) : stages.length === 0 ? (
              <div className="p-12 text-center bg-white rounded-2xl text-slate-400">
                No stages found. Add one or seed sample data.
              </div>
            ) : (
              <div className="grid md:grid-cols-2 gap-4">
                {stages.map((stage) => (
                  <div
                    key={stage.stageId}
                    className="bg-white p-5 rounded-2xl border shadow-sm hover:shadow-md transition-shadow"
                  >
                    {/* Card Header */}
                    <div className="flex justify-between items-center mb-2">
                      <span
                        className={`text-xs font-semibold px-2 py-1 rounded-full ${DIFFICULTY_BADGE[stage.difficulty]}`}
                      >
                        {stage.difficulty.charAt(0).toUpperCase() + stage.difficulty.slice(1)}
                      </span>
                      <div className="flex gap-2">
                        <button
                          onClick={() => startEdit(stage)}
                          className="text-sm text-indigo-600 hover:text-indigo-800"
                        >
                          ✏
                        </button>
                        <button
                          onClick={() => handleDelete(stage.stageId)}
                          className="text-sm text-red-500 hover:text-red-700"
                        >
                          🗑
                        </button>
                      </div>
                    </div>

                    <p className="text-lg font-black mb-2">Stage {stage.stageNumber}</p>

                    {/* Word Preview */}
                    <div className="grid grid-cols-2 gap-x-4 gap-y-1 mb-1">
                      {stage.words.slice(0, 4).map((w) => (
                        <span key={w.wordId} className="text-sm text-slate-500 truncate">
                          {w.word} — {w.meaning}
                        </span>
                      ))}
                    </div>
                    {stage.words.length > 4 && (
                      <p className="text-xs text-slate-400">
                        + {stage.words.length - 4} more words
                      </p>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
