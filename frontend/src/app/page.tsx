'use client';

import { useState } from 'react';
import { DashboardView } from '../features/dashboard/components/DashboardView';
import { UploadModal } from '../features/uploads/components/UploadModal';
import PlatformLayout from './(platform)/layout';

export default function Home() {
  const [isUploadOpen, setIsUploadOpen] = useState(false);

  return (
    <PlatformLayout>
      <DashboardView onOpenUpload={() => setIsUploadOpen(true)} />
      <UploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
      />
    </PlatformLayout>
  );
}
