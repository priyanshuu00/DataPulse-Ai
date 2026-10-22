export type VersionStatus = 
  | 'UPLOADING'
  | 'UPLOADED'
  | 'VALIDATING'
  | 'PROCESSING'
  | 'READY'
  | 'FAILED';

export interface Dataset {
  id: string;
  name: string;
  description: string | null;
  workspaceId?: string;
  dataSourceId?: string;
  createdBy?: string;
  status?: VersionStatus;
  createdAt: string;
  updatedAt: string;
}

export interface DatasetVersion {
  id: string;
  datasetId: string;
  semanticVersion: string;
  originalFilename: string;
  objectKey?: string;
  uri?: string;
  size?: number;
  checksum?: string;
  status: VersionStatus;
  createdAt: string;
  updatedAt?: string;
  schemaId?: string | null;
  rowCount?: number | null;
  createdBy?: string;
}

export interface ColumnMetadata {
  id: string;
  name: string;
  dataType: string;
  isNullable: boolean;
  description?: string | null;
  nullCount?: number | null;
  sampleValues?: string | null;
}

export interface DatasetSchema {
  id: string;
  datasetId: string;
  versionId: string;
  columns: ColumnMetadata[];
  createdAt?: string;
}

export interface DatasetCreateInput {
  name: string;
  description?: string;
  workspaceId?: string;
  dataSourceId?: string;
}
