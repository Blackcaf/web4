export interface PointRequest {
  x: number;
  y: number;
  r: number;
}

export interface Result {
  id: number;
  x: number;
  y: number;
  r: number;
  hit: boolean;
  timestamp: string;
  executionTime: string;
}

