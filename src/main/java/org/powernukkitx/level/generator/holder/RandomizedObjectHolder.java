package org.powernukkitx.level.generator.holder;

import org.powernukkitx.utils.random.RandomSourceProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RandomizedObjectHolder extends ObjectHolder {

    protected RandomSourceProvider randomSourceProvider;

}
