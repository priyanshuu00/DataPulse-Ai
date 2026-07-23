'use client';

import { useState } from 'react';
import { Navbar } from '../../components/layout/Navbar';
import { Sidebar } from '../../components/layout/Sidebar';
import { Breadcrumbs } from '../../components/layout/Breadcrumbs';
import { UploadModal } from '../../features/uploads/components/UploadModal';

export default function PlatformLayout({ children }: { children: React.ReactNode }) {
  const [isUploadOpen, setIsUploadOpen] = useState(false);

  return (
    <div className="min-h-screen flex flex-col bg-slate-100 dark:bg-slate-950 text-slate-900 dark:text-slate-100">
      <Navbar />
      <div className="flex flex-1">
        <Sidebar onOpenUpload={() => setIsUploadOpen(true)} />
        <main className="flex-1 p-6 overflow-y-auto max-w-7xl">
          <Breadcrumbs />
          <div className="mt-4">{children}</div>
        </main>
      </div>

      <UploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
        onSuccess={() => {
          setIsUploadOpen(false);
        }}
      />
    </div>
  );
}
