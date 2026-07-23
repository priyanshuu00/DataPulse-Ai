'use client';

import { useState } from 'react';
import { DatasetListView } from '../../../features/datasets/components/DatasetListView';
import { UploadModal } from '../../../features/uploads/components/UploadModal';

export default function DatasetsPage() {
  const [isUploadOpen, setIsUploadOpen] = useState(false);

  return (
    <>
      <DatasetListView onOpenUpload={() => setIsUploadOpen(true)} />
      <UploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
      />
    </>
  );
}
