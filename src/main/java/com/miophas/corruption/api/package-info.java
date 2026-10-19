/**
 * Extension contracts for Corruption. This package tree never depends on the mod's common implementation.
 *
 * <p>Existing rule/value types exposed in signatures still reside in core for source compatibility. Those
 * types, including their nested types, public/protected signatures and record components, are published
 * contracts rather than private implementation. The compatibility baseline is checked by
 * PublishedCoreContractTest. Additions are allowed; removals, changed signatures and newly exposed core
 * types require an explicit compatibility decision. New mutable mechanics belong behind ports or factories.
 *
 * <p>BossSkillRegistry registers individual factories; ArcaneCasting registers individual actions.
 * Whole-provider install methods remain as deprecated compatibility entry points. Client source animation
 * execution is installed through RemnantAnimationApi.Playback and is never executed by the core policy.
 */
package com.miophas.corruption.api;
