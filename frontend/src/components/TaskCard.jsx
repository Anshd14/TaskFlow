import { Draggable } from '@hello-pangea/dnd';
import { Flame, Minus, ChevronDown } from 'lucide-react';

const PRIORITY_STYLES = {
  HIGH: { label: 'High', className: 'bg-red-50 text-red-700 border-red-100' },
  MEDIUM: { label: 'Medium', className: 'bg-amber-50 text-amber-700 border-amber-100' },
  LOW: { label: 'Low', className: 'bg-zinc-100 text-zinc-600 border-zinc-200' },
};

export default function TaskCard({ task, index }) {
  const priority = PRIORITY_STYLES[task.priority] || PRIORITY_STYLES.LOW;

  return (
    <Draggable draggableId={`${task.id}`} index={index}>
      {(provided, snapshot) => (
        <div
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          className={`bg-white border border-zinc-200 rounded-lg p-3 mb-2 cursor-grab active:cursor-grabbing transition-shadow ${
            snapshot.isDragging ? 'shadow-lg ring-2 ring-accent/30' : 'hover:border-zinc-300'
          }`}
        >
          <p className="text-sm font-medium text-ink mb-2">{task.title}</p>
          {task.description && (
            <p className="text-xs text-zinc-500 mb-2 line-clamp-2">{task.description}</p>
          )}
          <div className="flex items-center justify-between">
            <span className={`text-xs font-medium px-2 py-0.5 rounded-full border ${priority.className}`}>
              {priority.label}
            </span>
            {task.assigneeEmail && (
              <span className="w-6 h-6 rounded-full bg-accent/10 text-accent text-xs font-semibold flex items-center justify-center">
                {task.assigneeEmail[0].toUpperCase()}
              </span>
            )}
          </div>
        </div>
      )}
    </Draggable>
  );
}
