package com.mecanicadm.workorder_api.shared.usecase;

public interface UseCase<I, O> {
    O execute(I input);
}
