export interface RuleRecommendationResponse {
  id: string;
  columnName: string;
  ruleType: string;
  parameters: Record<string, string>;
  explanation: string;
  confidenceScore: number;
  source: 'RULE_ENGINE' | 'AI';
  generatedAt: string;
}
