'use client';

import { useState } from 'react';
import { DashboardView } from '../../features/dashboard/components/DashboardView';
import { UploadModal } from '../../features/uploads/components/UploadModal';

export default function DashboardPage() {
  const [isUploadOpen, setIsUploadOpen] = useState(false);

  return (
    <>
      <DashboardView onOpenUpload={() => setIsUploadOpen(true)} />
      <UploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
      />
    </>
  );
}
