import { useState, useEffect, useCallback } from 'react';
import { DragDropContext, Droppable } from '@hello-pangea/dnd';
import { LayoutGrid, Plus, LogOut, Folder, ChevronDown, X } from 'lucide-react';
import client from '../api/client';
import { useAuth } from '../context/AuthContext';
import TaskCard from '../components/TaskCard';
import TaskModal from '../components/TaskModal';

const COLUMNS = [
  { status: 'TODO', label: 'To do' },
  { status: 'IN_PROGRESS', label: 'In progress' },
  { status: 'DONE', label: 'Done' },
];

export default function Dashboard() {
  const { email, logout } = useAuth();
  const [workspaces, setWorkspaces] = useState([]);
  const [activeWorkspace, setActiveWorkspace] = useState(null);
  const [projects, setProjects] = useState([]);
  const [activeProject, setActiveProject] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [showTaskModal, setShowTaskModal] = useState(false);
  const [newWorkspaceName, setNewWorkspaceName] = useState('');
  const [newProjectName, setNewProjectName] = useState('');
  const [showWsInput, setShowWsInput] = useState(false);
  const [showProjInput, setShowProjInput] = useState(false);

  const loadWorkspaces = useCallback(async () => {
    const { data } = await client.get('/workspaces');
    setWorkspaces(data);
    if (data.length > 0 && !activeWorkspace) setActiveWorkspace(data[0]);
  }, [activeWorkspace]);

  const loadProjects = useCallback(async (workspaceId) => {
    const { data } = await client.get(`/workspaces/${workspaceId}/projects`);
    setProjects(data);
    if (data.length > 0) setActiveProject(data[0]);
    else setActiveProject(null);
  }, []);

  const loadTasks = useCallback(async (projectId) => {
    const { data } = await client.get(`/projects/${projectId}/tasks`, { params: { size: 100 } });
    setTasks(data.content || []);
  }, []);

  useEffect(() => { loadWorkspaces(); }, []);
  useEffect(() => { if (activeWorkspace) loadProjects(activeWorkspace.id); }, [activeWorkspace]);
  useEffect(() => {
    if (activeProject) loadTasks(activeProject.id);
    else setTasks([]);
  }, [activeProject]);

  const createWorkspace = async () => {
    if (!newWorkspaceName.trim()) return;
    const { data } = await client.post('/workspaces', { name: newWorkspaceName });
    setWorkspaces((prev) => [...prev, data]);
    setActiveWorkspace(data);
    setNewWorkspaceName('');
    setShowWsInput(false);
  };

  const createProject = async () => {
    if (!newProjectName.trim() || !activeWorkspace) return;
    const { data } = await client.post(`/workspaces/${activeWorkspace.id}/projects`, { name: newProjectName });
    setProjects((prev) => [...prev, data]);
    setActiveProject(data);
    setNewProjectName('');
    setShowProjInput(false);
  };

  const createTask = async ({ title, description, priority }) => {
    const { data } = await client.post(`/projects/${activeProject.id}/tasks`, { title, description, priority });
    setTasks((prev) => [...prev, data]);
  };

  const handleDragEnd = async (result) => {
    const { destination, draggableId } = result;
    if (!destination) return;
    const newStatus = destination.droppableId;
    const taskId = Number(draggableId);

    setTasks((prev) => prev.map((t) => (t.id === taskId ? { ...t, status: newStatus } : t)));

    try {
      await client.put(`/projects/${activeProject.id}/tasks/${taskId}/status`, null, {
        params: { status: newStatus },
      });
    } catch {
      loadTasks(activeProject.id);
    }
  };

  return (
    <div className="min-h-screen flex bg-surface">
      <aside className="w-64 bg-ink text-zinc-300 flex flex-col shrink-0">
        <div className="flex items-center gap-2 px-5 py-5 border-b border-zinc-800">
          <LayoutGrid className="w-5 h-5 text-accent" strokeWidth={2.5} />
          <span className="text-white font-semibold text-sm">TaskFlow</span>
        </div>

        <div className="flex-1 overflow-y-auto px-3 py-4">
          <div className="flex items-center justify-between px-2 mb-2">
            <span className="text-xs font-semibold uppercase tracking-wide text-zinc-500">Workspaces</span>
            <button onClick={() => setShowWsInput(!showWsInput)} className="text-zinc-500 hover:text-white">
              <Plus className="w-3.5 h-3.5" />
            </button>
          </div>
          {showWsInput && (
            <div className="flex gap-1 px-2 mb-2">
              <input
                autoFocus
                value={newWorkspaceName}
                onChange={(e) => setNewWorkspaceName(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && createWorkspace()}
                placeholder="Workspace name"
                className="flex-1 bg-zinc-800 text-white text-xs rounded px-2 py-1 outline-none"
              />
            </div>
          )}
          <div className="space-y-0.5 mb-5">
            {workspaces.map((ws) => (
              <button
                key={ws.id}
                onClick={() => setActiveWorkspace(ws)}
                className={`w-full text-left text-sm px-2 py-1.5 rounded-md transition-colors ${
                  activeWorkspace?.id === ws.id ? 'bg-accent text-white' : 'hover:bg-zinc-800 text-zinc-300'
                }`}
              >
                {ws.name}
              </button>
            ))}
          </div>

          {activeWorkspace && (
            <>
              <div className="flex items-center justify-between px-2 mb-2">
                <span className="text-xs font-semibold uppercase tracking-wide text-zinc-500">Projects</span>
                <button onClick={() => setShowProjInput(!showProjInput)} className="text-zinc-500 hover:text-white">
                  <Plus className="w-3.5 h-3.5" />
                </button>
              </div>
              {showProjInput && (
                <div className="flex gap-1 px-2 mb-2">
                  <input
                    autoFocus
                    value={newProjectName}
                    onChange={(e) => setNewProjectName(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && createProject()}
                    placeholder="Project name"
                    className="flex-1 bg-zinc-800 text-white text-xs rounded px-2 py-1 outline-none"
                  />
                </div>
              )}
              <div className="space-y-0.5">
                {projects.map((p) => (
                  <button
                    key={p.id}
                    onClick={() => setActiveProject(p)}
                    className={`w-full flex items-center gap-2 text-left text-sm px-2 py-1.5 rounded-md transition-colors ${
                      activeProject?.id === p.id ? 'bg-zinc-800 text-white' : 'hover:bg-zinc-800 text-zinc-400'
                    }`}
                  >
                    <Folder className="w-3.5 h-3.5 shrink-0" />
                    <span className="truncate">{p.name}</span>
                  </button>
                ))}
              </div>
            </>
          )}
        </div>

        <div className="px-3 py-3 border-t border-zinc-800 flex items-center justify-between">
          <div className="flex items-center gap-2 min-w-0">
            <span className="w-7 h-7 rounded-full bg-accent flex items-center justify-center text-white text-xs font-semibold shrink-0">
              {email?.[0]?.toUpperCase()}
            </span>
            <span className="text-xs text-zinc-400 truncate">{email}</span>
          </div>
          <button onClick={logout} className="text-zinc-500 hover:text-white shrink-0">
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </aside>

      <main className="flex-1 flex flex-col min-w-0">
        {activeProject ? (
          <>
            <div className="flex items-center justify-between px-8 py-5 border-b border-zinc-200">
              <h1 className="text-lg font-semibold text-ink">{activeProject.name}</h1>
              <button
                onClick={() => setShowTaskModal(true)}
                className="flex items-center gap-1.5 bg-accent hover:bg-accent-dark text-white text-sm font-medium px-3 py-2 rounded-lg"
              >
                <Plus className="w-4 h-4" /> New task
              </button>
            </div>

            <DragDropContext onDragEnd={handleDragEnd}>
              <div className="flex-1 grid grid-cols-3 gap-4 p-6 overflow-x-auto">
                {COLUMNS.map((col) => {
                  const colTasks = tasks.filter((t) => t.status === col.status);
                  return (
                    <div key={col.status} className="bg-zinc-100/60 rounded-xl p-3 flex flex-col min-w-[260px]">
                      <div className="flex items-center justify-between mb-3 px-1">
                        <span className="text-sm font-semibold text-zinc-700">{col.label}</span>
                        <span className="text-xs text-zinc-400 bg-white rounded-full px-2 py-0.5">{colTasks.length}</span>
                      </div>
                      <Droppable droppableId={col.status}>
                        {(provided) => (
                          <div ref={provided.innerRef} {...provided.droppableProps} className="flex-1 min-h-[60px]">
                            {colTasks.map((task, i) => (
                              <TaskCard key={task.id} task={task} index={i} />
                            ))}
                            {provided.placeholder}
                          </div>
                        )}
                      </Droppable>
                    </div>
                  );
                })}
              </div>
            </DragDropContext>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center text-zinc-400 text-sm">
            {workspaces.length === 0
              ? 'Create a workspace to get started.'
              : 'Create a project to see your board.'}
          </div>
        )}
      </main>

      {showTaskModal && (
        <TaskModal onClose={() => setShowTaskModal(false)} onCreate={createTask} />
      )}
    </div>
  );
}
