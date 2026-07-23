import { VersionStatus } from './dataset';

export interface PipelineJob {
  id: string;
  datasetId: string;
  datasetName: string;
  versionId: string;
  semanticVersion: string;
  currentStage: VersionStatus;
  kafkaTopicStatus: 'HEALTHY' | 'SYNCING' | 'IDLE';
  sparkJobStatus: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';
  processingTimeSeconds: number;
  failureReason?: string | null;
  retryCount: number;
  startedAt: string;
  updatedAt: string;
}
