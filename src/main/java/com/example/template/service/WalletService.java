package com.example.template.service;

import com.example.template.config.OctetConfig;
import com.example.template.domain.MemberEntity;
import com.example.template.domain.WalletEntity;
import com.example.template.dto.Auth;
import com.example.template.dto.octet.Address;
import com.example.template.enums.MemberType;
import com.example.template.enums.SymbolType;
import com.example.template.exception.CustomResponseStatusException;

import com.example.template.repository.WalletRepository;
import com.example.template.utils.AesUtils;
import com.example.template.utils.HashUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final OctetConfig octetConfig;
    private final OctetService octetService;

    @Transactional(noRollbackFor = CustomResponseStatusException.class)
    public WalletEntity createWallet (MemberEntity memberEntity) {
        log.info("OCTET ADDRESS = " + octetConfig.baseUrl);
        // 옥텟 지갑 기본 생성
        // 모두 기본값으로 설정
        Address.ChildAddressResponse response = octetService.createBtrChildAddress();
        // 생성된 지갑은 1개이므로 확인한다.
        Address.ChildAddress childAddress = response.getAddresses().get(0);

        WalletEntity walletEntity = WalletEntity.builder()
                .memberId(memberEntity.getId())
                .symbol(SymbolType.HTC)
                .address(childAddress.getAddress())
                .balance(BigDecimal.ZERO)
                .build();

       return walletRepository.save(walletEntity);
    }
}
