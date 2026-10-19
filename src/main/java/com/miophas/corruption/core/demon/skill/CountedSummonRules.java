package com.miophas.corruption.core.demon.skill;

/** Summoning with an explicit radius within which existing summons count against the limit. */
public record CountedSummonRules(SummonRules rules, double radius) {}
