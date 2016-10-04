package de.cneubauer.transformation;

import io.konik.zugferd.Invoice;
import io.konik.zugferd.entity.*;
import io.konik.zugferd.entity.trade.*;
import io.konik.zugferd.entity.trade.item.*;
import io.konik.zugferd.unece.codes.TaxCode;
import io.konik.zugferd.unqualified.*;

import java.math.BigDecimal;

import static com.neovisionaries.i18n.CountryCode.DE;
import static com.neovisionaries.i18n.CurrencyCode.EUR;
import static io.konik.zugferd.profile.ConformanceLevel.BASIC;
import static io.konik.zugferd.unece.codes.DocumentCode._380;
import static io.konik.zugferd.unece.codes.Reference.FC;
import static io.konik.zugferd.unece.codes.UnitOfMeasurement.UNIT;
import static org.apache.commons.lang3.time.DateUtils.addMonths;

/**
 * Created by Christoph Neubauer on 21.09.2016.
 *
 */
public class ZugFerdTransformator {

    public Invoice createMockInvoice() {

        ZfDate today = new ZfDateDay();
        ZfDate nextMonth = new ZfDateMonth(addMonths(today, 1));

        Invoice invoice = new Invoice(BASIC);
        invoice.setHeader(new Header()
                .setInvoiceNumber("20131122-42")
                .setCode(_380)
                .setIssued(today)
                .setName("Rechnung"));

        Trade trade = new Trade();
        trade.setAgreement(new Agreement()
                .setSeller(new TradeParty()
                        .setName("Seller Inc.")
                        .setAddress(new Address("80331", "Marienplatz 1", "München", DE))
                        .addTaxRegistrations(new TaxRegistration("DE122...", FC)))
                .setBuyer(new TradeParty()
                        .setName("Buyer Inc.")
                        .setAddress(new Address("50667", "Domkloster 4", "Köln", DE))
                        .addTaxRegistrations(new TaxRegistration("DE123...", FC))));

        trade.setDelivery(new Delivery(nextMonth));

        ItemTax itemTax = new ItemTax();
        itemTax.setPercentage(BigDecimal.valueOf(19));
        itemTax.setType(TaxCode.VAT);

        trade.addItem(new Item()
                .setProduct(new Product().setName("Saddle"))
                .setAgreement(new SpecifiedAgreement().setGrossPrice(new GrossPrice(new Amount(100, EUR))).setNetPrice(new Price(new Amount(100, EUR))))
                .setSettlement(new SpecifiedSettlement().addTradeTax(itemTax))
                .setDelivery(new SpecifiedDelivery(new Quantity(1, UNIT))));

        trade.setSettlement(new Settlement()
                .setPaymentReference("20131122-42")
                .setCurrency(EUR)
                .addPaymentMeans(new PaymentMeans()
                        .setPayerAccount(new DebtorFinancialAccount("DE01234.."))
                        .setPayerInstitution(new FinancialInstitution("GENO...")))
                .setMonetarySummation(new MonetarySummation()
                        .setLineTotal(new Amount(100, EUR))
                        .setChargeTotal(new Amount(0,EUR))
                        .setAllowanceTotal(new Amount(0, EUR))
                        .setTaxBasisTotal(new Amount(100, EUR))
                        .setTaxTotal(new Amount(19, EUR))
                        .setDuePayable(new Amount(119, EUR))
                        .setTotalPrepaid(new Amount(0, EUR))
                        .setGrandTotal(new Amount(119, EUR))));

        invoice.setTrade(trade);

        return invoice;
    }
}