'use client';

import { use } from 'react';
import { DatasetDetailView } from '../../../../features/datasets/components/DatasetDetailView';

export default function DatasetDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const resolvedParams = use(params);
  return <DatasetDetailView datasetId={resolvedParams.id} />;
}
